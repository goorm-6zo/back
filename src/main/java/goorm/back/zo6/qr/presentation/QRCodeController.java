package goorm.back.zo6.qr.presentation;

import goorm.back.zo6.qr.application.QRCodeResponse;
import goorm.back.zo6.qr.application.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/qr")
@RequiredArgsConstructor
public class QRCodeController {
    private final QRCodeService qrCodeService;

    @GetMapping
    public ResponseEntity<QRCodeResponse> getQRCode(
            @RequestParam(name = "conferenceId") Long conferenceId,
            @RequestParam(name = "sessionId", required = false) Long sessionId,
            @RequestParam(name = "url") String url) {
        return ResponseEntity.ok(qrCodeService.createQRCode(conferenceId, sessionId, url));
    }
}
