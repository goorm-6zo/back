package goorm.back.zo6.qr.application;

public interface QRCodeService {
    QRCodeResponse createQRCode(Long conferenceId, Long sectionId, String url);
}
