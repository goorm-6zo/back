package goorm.back.zo6.face.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.face.application.FaceManagementService;
import goorm.back.zo6.face.dto.response.FaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
public class FaceManagementController {

    private final FaceManagementService faceImageStorageService;

    // s3에 이미지 업로드, rekognition collection 에 저장
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 이미지 업로드 요청", description = "유저의 얼굴 이미지를 s3, collection 에 저장합니다.")
    public ResponseEntity<FaceResponse> getUploadPreSignedUrl(@AuthenticationPrincipal LoginUser loginUser,
                                                              @RequestParam("faceImage") MultipartFile faceImage) {
        Long userId = loginUser.user().getId();
        FaceResponse response = faceImageStorageService.uploadUserFace(userId, faceImage);
        return ResponseEntity.ok(response);
    }

    // 저장된 얼굴 이미지의 다운로드 URL을 반환
    @GetMapping
    @Operation(summary = "등록된 얼굴 이미지 조회", description = "유저의 얼굴 이미지 다운로드 Presigned URL을 반환합니다.")
    public ResponseEntity<Map<String, String>> getUserFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        String faceImageUrl = faceImageStorageService.getFaceImageUrl(userId);
        return ResponseEntity.ok(Map.of("imageUrl", faceImageUrl));
    }

    // 얼굴 이미지 삭제
    @DeleteMapping
    @Operation(summary = "얼굴 이미지 삭제", description = "유저의 얼굴 이미지를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        faceImageStorageService.deleteFaceImage(userId);
        return ResponseEntity.ok(Map.of("message", "얼굴 이미지 삭제 완료"));
    }
}
