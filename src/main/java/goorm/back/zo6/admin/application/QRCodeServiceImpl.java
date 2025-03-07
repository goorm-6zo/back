package goorm.back.zo6.admin.application;

import goorm.back.zo6.admin.domain.IdExistenceDao;
import goorm.back.zo6.admin.domain.QRCodeGenerator;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
class QRCodeServiceImpl implements QRCodeService {
    private final IdExistenceDao conferenceidExistenceDao;
    private final IdExistenceDao sessionIdExistenceDao;
    private final QRCodeGenerator qrCodeGenerator;
    private final QRContentBuilder qrContentBuilder;

    public QRCodeServiceImpl(
            @Qualifier("conferenceIdExistenceDao") IdExistenceDao conferenceIdExistenceDao,
            @Qualifier("sessionIdExistenceDao") IdExistenceDao sessionIdExistenceDao,
            QRCodeGenerator qrCodeGenerator,
            QRContentBuilder qrContentBuilder
    ) {
        this.conferenceidExistenceDao = conferenceIdExistenceDao;
        this.sessionIdExistenceDao = sessionIdExistenceDao;
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrContentBuilder = qrContentBuilder;
    }

    @Override
    public QRCodeResponse createQRCode(Long conferenceId, Long sectionId, String url) {
        validateConferenceId(conferenceId);
        validateSectionId(sectionId);

        String qrContent = qrContentBuilder.build(conferenceId, sectionId, url);
        String qrImage = qrCodeGenerator.generate(qrContent);
        return new QRCodeResponse(qrImage);
    }

    private void validateConferenceId(Long conferenceId) {
        if (!conferenceidExistenceDao.exists(conferenceId))
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUNT);
    }

    private void validateSectionId(Long sessionId) {
        if (sessionId != null && !sessionIdExistenceDao.exists(sessionId))
            throw new CustomException(ErrorCode.SESSION_NOT_FOUNT);
    }
}
