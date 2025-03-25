package goorm.back.zo6.face.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.common.dto.ResponseDto;
import goorm.back.zo6.face.application.FaceRecognitionService;
import goorm.back.zo6.face.dto.response.FaceAuthResultResponse;
import goorm.back.zo6.face.dto.response.FaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "face", description = "Face API")
@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
@Log4j2
public class FaceRecognitionController {
    private final FaceRecognitionService rekognitionService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 이미지 업로드 요청(유저 얼굴 등록)", description = "유저의 얼굴 이미지를 collection 에 저장합니다. <br>" +
            "얼굴 업로드 정보는 face 테이블에 저장됩니다.")
    public ResponseEntity<ResponseDto<FaceResponse>> getUploadPreSignedUrl(@AuthenticationPrincipal LoginUser loginUser,
                                                              @RequestParam("faceImage") MultipartFile faceImage) {
        Long userId = loginUser.getId();
        FaceResponse response = rekognitionService.uploadUserFace(userId, faceImage);
        return ResponseEntity.ok(ResponseDto.of(response));
    }

    // 얼굴 이미지 삭제
    @DeleteMapping(value = "/delete")
    @Operation(summary = "얼굴 이미지 삭제", description = "유저의 얼굴 이미지를 collection 에서 삭제합니다.<br>" +
            "얼굴 정보는 face 테이블에서 삭제됩니다.")
    public ResponseEntity<ResponseDto<String>> deleteFaceImage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getId();
        rekognitionService.deleteUserFace(userId);
        return ResponseEntity.ok(ResponseDto.of("얼굴 이미지 삭제 완료"));
    }

    // 얼굴 인증
    @PostMapping(value = "/authentication", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "얼굴 인증", description = "유저의 얼굴 이미지를 받아서 인증합니다. <br>" +
            "컨퍼런스 입장 시 conferenceId 값은 필수이며 sessionId 값을 비워서 보내면 됩니다. <br>" +
            "세션 입장 시 conferenceId 값과 sessionId 값은 필수입니다.")
    public ResponseEntity<ResponseDto<FaceAuthResultResponse>> authenticationByUserFace(
            @RequestParam(name = "conferenceId") Long conferenceId,
            @RequestParam(name = "sessionId", required = false) Long sessionId,
            @RequestPart("faceImage") MultipartFile faceImage) {
        FaceAuthResultResponse result = rekognitionService.authenticationByUserFace(conferenceId, sessionId, faceImage);
        return ResponseEntity.ok(ResponseDto.of(result));
    }

    // Rekognition Collection 생성
    @PostMapping("/collection")
    @Operation(summary = "Rekognition Collection 생성", description = "Rekognition Collection 을 생성합니다. <br>" +
            "aws collection 이 없을 때 단 한번만 호출하면 됩니다. swagger 에서 호출 해도 무방합니다.<br>" +
            "collection 이 존재 할때 호출 하면 예외가 발생되고 서버에 문제가 생기진 않습니다.")
    public ResponseEntity<ResponseDto<String>> createCollection() {
        rekognitionService.createCollection();
        return ResponseEntity.ok(ResponseDto.of("Rekognition Collection 생성 완료!"));
    }

    @DeleteMapping("/collection")
    @Operation(summary = "Rekognition Collection 삭제", description = "Rekognition Collection 을 삭제합니다. <br>" +
            "서버의 일관성을 보장하기 위해, rekognition collection 삭제 후 재 생성해주세요 ")
    public ResponseEntity<ResponseDto<String>> deleteCollection(){
        rekognitionService.deleteCollection();
        return ResponseEntity.ok(ResponseDto.of("Rekognition Collection 삭제 완료!"));
    }
}
