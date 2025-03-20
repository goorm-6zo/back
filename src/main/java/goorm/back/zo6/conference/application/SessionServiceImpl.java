package goorm.back.zo6.conference.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public SessionResponse updateSession(Long sessionId, String location) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        session.updateSession(location);

        return new SessionResponse(
                session.getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getStartTime(),
                session.getEndTime(),
                session.getSummary(),
                session.getSpeakerName(),
                session.getSpeakerOrganization(),
                session.isActive(),
                session.getSpeakerImageKey());
    }
}
