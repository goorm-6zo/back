package goorm.back.zo6.conference.application.shared;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionValidator {

    private final SessionRepository sessionRepository;

    public Session getSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
    }

    public Session findSessionByConferenceAndSessionId(Long conferenceId, Long sessionId) {
        return sessionRepository.findByConferenceIdAndSessionId(conferenceId, sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUNT));
    }
}
