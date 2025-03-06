package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    public List<Session> getSessionsByConferenceId(Long conferenceId) {
        return sessionRepository.findByConferenceId(conferenceId);
    }

    public Session getSession(Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found"));
    }

    public boolean isSessionReservable(Long sessionId) {
        Session session = getSession(sessionId);
        return session.isReservable();
    }

    public boolean areSessionsReservable(Long conferenceId, List<Long> sessionIds) {
        List<Session> sessions = getSessionsByConferenceId(conferenceId);
        return sessions.stream()
                .filter(session -> sessionIds.contains(session.getId()))
                .allMatch(Session::isReservable);
    }
}
