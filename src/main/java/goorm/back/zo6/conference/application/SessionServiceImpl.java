package goorm.back.zo6.conference.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public SessionResponse updateSession(Long sessionId, String name, Integer capacity, String location, LocalDateTime time, String summary, String speakerName, String speakerOrganization, boolean isActive) {
        Session session = sessionRepository.findById(sessionId).orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        session.updateSession(name, capacity, location, time, summary, speakerName, speakerOrganization, isActive);

        return new SessionResponse(session.getId(), session.getName(), session.getCapacity(), session.getLocation(), session.getTime(), session.getSummary(), session.getSpeakerName(), session.getSpeakerOrganization(), session.isActive());
    }
}
