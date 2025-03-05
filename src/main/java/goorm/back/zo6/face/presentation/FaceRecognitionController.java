package goorm.back.zo6.face.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.face.application.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
public class FaceRecognitionController {

    private final S3Service s3Service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 이미지 등록",description = "유저의 얼굴 이미지를 저장합니다.")
    public ResponseEntity<String> uploadFaceImage(@AuthenticationPrincipal LoginUser loginUser, @RequestPart("file") MultipartFile file) {
        Long userId = loginUser.user().getId();
        String faceId = s3Service.uploadFaceImage(userId, file);
        return ResponseEntity.ok("얼굴 등록 완료! Face ID: " + faceId);
    }

    @GetMapping("/images")
    public ResponseEntity<List<String>> getUserFaceImages(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        return ResponseEntity.ok(s3Service.getUserFaceImages(userId));
    }
}
