package goorm.back.zo6.face.presentation;

import goorm.back.zo6.face.application.RekognitionService;
import goorm.back.zo6.face.dto.response.FaceAuthResultResponse;
import goorm.back.zo6.face.dto.response.FaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/rekognition")
@RequiredArgsConstructor
@Log4j2
public class RekognitionController {
    private final RekognitionService rekognitionService;

    // 얼굴 인증
    @PostMapping(value = "/authentication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 인증", description = "유저의 얼굴 이미지를 받아서 인증합니다.")
    public ResponseEntity<FaceAuthResultResponse> authenticationByUserFace(@RequestParam("faceImage") MultipartFile faceImage) throws IOException {
        FaceAuthResultResponse result = rekognitionService.authenticationByUserFace(faceImage);
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
