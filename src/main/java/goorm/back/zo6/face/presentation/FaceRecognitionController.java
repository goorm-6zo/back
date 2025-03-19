package goorm.back.zo6.face.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.face.application.FaceRecognitionService;
import goorm.back.zo6.face.dto.response.FaceAuthResultResponse;
import goorm.back.zo6.face.dto.response.FaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
@Log4j2
public class FaceRecognitionController {
    private final FaceRecognitionService rekognitionService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 이미지 업로드 요청", description = "유저의 얼굴 이미지를 collection 에 저장합니다.")
    public ResponseEntity<FaceResponse> getUploadPreSignedUrl(@AuthenticationPrincipal LoginUser loginUser,
                                                              @RequestParam("faceImage") MultipartFile faceImage) {
        Long userId = loginUser.getId();
        FaceResponse response = rekognitionService.uploadUserFace(userId, faceImage);
        return ResponseEntity.ok(response);
    }

    // 얼굴 이미지 삭제
    @DeleteMapping(value = "/delete")
    @Operation(summary = "얼굴 이미지 삭제", description = "유저의 얼굴 이미지를 삭제합니다.")
    public ResponseEntity<Map<String, String>> deleteFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        rekognitionService.deleteFaceImage(userId);
        return ResponseEntity.ok(Map.of("message", "얼굴 이미지 삭제 완료"));
    }

    // 얼굴 인증
    @PostMapping(value = "/authentication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 인증", description = "유저의 얼굴 이미지를 받아서 인증합니다.")
    public ResponseEntity<FaceAuthResultResponse> authenticationByUserFace(
            @RequestParam(name = "conferenceId") Long conferenceId,
            @RequestParam(name = "sessionId", required = false) Long sessionId,
            @RequestPart("faceImage") MultipartFile faceImage) {
        FaceAuthResultResponse result = rekognitionService.authenticationByUserFace(conferenceId, sessionId, faceImage);
        return ResponseEntity.ok(result);
    }

    // Rekognition Collection 생성
    @PostMapping("/collection")
    @Operation(summary = "Rekognition Collection 생성", description = "Rekognition Collection 을 생성합니다.")
    public ResponseEntity<String> createCollection() {
        rekognitionService.createCollection();
        return ResponseEntity.ok("Rekognition Collection 생성 완료!");
    }
}
