package goorm.back.zo6.admin.presentation;

import goorm.back.zo6.admin.application.QRCodeResponse;
import goorm.back.zo6.admin.application.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/qr")
@RequiredArgsConstructor
class QRCodeController {
    private final QRCodeService qrCodeService;

    @GetMapping
    public ResponseEntity<QRCodeResponse> getQRCode(
            @RequestParam(name = "conferenceId") Long conferenceId,
            @RequestParam(name = "sectionId", required = false) Long sectionId,
            @RequestParam(name = "url") String url) {
        return ResponseEntity.ok(qrCodeService.createQRCode(conferenceId, sectionId, url));
    }
}
