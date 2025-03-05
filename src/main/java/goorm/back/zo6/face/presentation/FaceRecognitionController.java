package goorm.back.zo6.face.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.face.application.FaceImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
public class FaceRecognitionController {

    private final FaceImageStorageService faceImageStorageService;

    // 프론트에서 s3에 직접 업로드, 서버에서는 Presigned Url을 제공
    @PostMapping("/upload")
    @Operation(summary = "얼굴 이미지 업로드 URL 요청", description = "유저의 얼굴 이미지를 업로드할 Presigned URL을 생성합니다.")
    public ResponseEntity<Map<String, String>> getUploadPreSignedUrl(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        String presignedUrl = faceImageStorageService.generateUploadPreSignedUrl(userId);
        return ResponseEntity.ok(Map.of("presignedUrl", presignedUrl));
    }

    // 저장된 얼굴 이미지의 다운로드 URL을 반환
    @GetMapping
    @Operation(summary = "등록된 얼굴 이미지 조회", description = "유저의 얼굴 이미지 다운로드 URL을 반환합니다.")
    public ResponseEntity<Map<String, String>> getUserFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        String faceImageUrl = faceImageStorageService.getFaceImageUrl(userId);
        return ResponseEntity.ok(Map.of("imageUrl", faceImageUrl));
    }

    // 얼굴 이미지 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.user().getId();
        faceImageStorageService.deleteFaceImage(userId);
        return ResponseEntity.ok(Map.of("message", "얼굴 이미지 삭제 완료"));
    }


    // 얼굴 비교 (서버에서 직접 비교) 유저가 업로드한 얼굴 이미지와 저장된 얼굴 이미지 비교
//    @PostMapping(value = "/compare", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "얼굴 비교", description = "유저가 업로드한 얼굴과 저장된 얼굴을 비교합니다.")
//    public ResponseEntity<Map<String, Boolean>> compareFace(
//            @AuthenticationPrincipal LoginUser loginUser,
//            @RequestPart("file") MultipartFile file) {
//        Long userId = loginUser.user().getId();
//        try {
//            boolean isMatch = faceRecognitionService.compareFace(userId, file.getBytes());
//            return ResponseEntity.ok(Map.of("match", isMatch));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(Map.of("error", "얼굴 비교 중 오류 발생"));
//        }
//    }


}
