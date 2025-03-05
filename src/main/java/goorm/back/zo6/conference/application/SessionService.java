package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<Session> getSessionsByConferenceId(Long conferenceId) {
        return sessionRepository.findByConferenceId(conferenceId);
    }

    public Session createSession(Session session) {
        return sessionRepository.save(session);
    }
}
