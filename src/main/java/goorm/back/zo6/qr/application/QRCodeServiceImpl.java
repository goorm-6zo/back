package goorm.back.zo6.qr.application;

import goorm.back.zo6.qr.domain.QRCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class QRCodeServiceImpl implements QRCodeService {
    private final QRValidator qrValidator;
    private final QRCodeGenerator qrCodeGenerator;
    private final QRContentBuilder qrContentBuilder;

    @Override
    public QRCodeResponse createQRCode(Long conferenceId, Long sectionId, String url) {
        qrValidator.validateConferenceId(conferenceId);
        qrValidator.validateSessionId(sectionId);

        String qrContent = qrContentBuilder.build(conferenceId, sectionId, url);
        String qrImage = qrCodeGenerator.generate(qrContent);

        return new QRCodeResponse(qrImage);
    }
}
