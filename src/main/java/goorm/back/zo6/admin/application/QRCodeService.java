package goorm.back.zo6.admin.application;

public interface QRCodeService {
    QRCodeResponse createQRCode(Long conferenceId, Long sectionId, String url);
}
