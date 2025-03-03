package goorm.back.zo6.admin.application;

public record QRCodeResponse(
    Long conferenceId,
    Long sectionId,
    String qrImageBase64
) {}
