package goorm.back.zo6.conference.application.command.session;

import goorm.back.zo6.conference.application.dto.SessionCreateRequest;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.shared.*;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionCommandServiceImpl implements SessionCommandService {

    private final SessionRepository sessionRepository;

    private final ConferenceValidator conferenceValidator;

    private final SessionValidator sessionValidator;

    private final ConferenceMapper conferenceMapper;

    private final SessionFactory sessionFactory;

    @Override
    public boolean getSessionStatus(Long conferenceId, Long sessionId) {

        Session session = sessionValidator.getSessionOrThrow(sessionId);
        return session.isActive();
    }

    @Override
    @Transactional
    public SessionDto updateSession(Long sessionId, String location) {

        Session session = sessionValidator.getSessionOrThrow(sessionId);
        session.updateSession(location);

        sessionRepository.save(session);

        return conferenceMapper.toSessionDto(session);
    }

    @Override
    public void updateSessionStatus(Long conferenceId, Long sessionId, boolean newStatus) {

        Session session = sessionValidator.findSessionByConferenceAndSessionId(conferenceId, sessionId);
        session.updateActive(newStatus);

        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public SessionDto createSession(SessionCreateRequest request) {

        Conference conference = conferenceValidator.findConferenceOrThrow(request.conferenceId());

        Session session = sessionFactory.createSession(request, conference);

        Session savedSession = sessionRepository.save(session);

        return conferenceMapper.toSessionDto(savedSession);
    }
}
