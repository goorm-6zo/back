package goorm.back.zo6.qr.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.qr.domain.IdExistenceDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class QRValidator {
    private final IdExistenceDao conferenceIdExistenceDao;
    private final IdExistenceDao sessionIdExistenceDao;

    public QRValidator(
            @Qualifier("conferenceIdExistenceDao") IdExistenceDao conferenceIdExistenceDao,
            @Qualifier("sessionIdExistenceDao") IdExistenceDao sessionIdExistenceDao
    ) {
        this.conferenceIdExistenceDao = conferenceIdExistenceDao;
        this.sessionIdExistenceDao = sessionIdExistenceDao;
    }

    public void validateConferenceId(Long conferenceId) {
        if (!conferenceIdExistenceDao.exists(conferenceId))
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
    }

    public void validateSessionId(Long sessionId) {
        if (sessionId != null && !sessionIdExistenceDao.exists(sessionId))
            throw new CustomException(ErrorCode.SESSION_NOT_FOUND);
    }
}
