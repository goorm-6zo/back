package goorm.back.zo6.qr.presentation;

import goorm.back.zo6.common.dto.ResponseDto;
import goorm.back.zo6.qr.application.QRCodeResponse;
import goorm.back.zo6.qr.application.QRCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "admin", description = "QR API")
@RestController
@RequestMapping("/api/v1/admin/qr")
@RequiredArgsConstructor
public class QRCodeController {
    private final QRCodeService qrCodeService;

    @GetMapping
    @Operation(summary = "QR 이미지 생성", description = "컨퍼런스, 세션, URL 정보를 바탕으로 QR코드를 생성합니다.")
    public ResponseEntity<ResponseDto<QRCodeResponse>> getQRCode(
            @RequestParam(name = "conferenceId") Long conferenceId,
            @RequestParam(name = "sessionId", required = false) Long sessionId,
            @RequestParam(name = "url") String url) {
        return ResponseEntity.ok(ResponseDto.of(qrCodeService.createQRCode(conferenceId, sessionId, url)));
    }
}
