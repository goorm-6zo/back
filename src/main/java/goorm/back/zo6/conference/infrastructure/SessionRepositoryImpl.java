package goorm.back.zo6.conference.infrastructure;

import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionJpaRepository sessionJpaRepository;

    @Override
    public Optional<Session> findById(Long id) {
        return sessionJpaRepository.findById(id);
    }

    @Override
    public Session save(Session session) {
        sessionJpaRepository.save(session);
        return session;
    }

    @Override
    public Optional<Session> findByConferenceIdAndSessionId(Long conferenceId, Long sessionId) {
        return sessionJpaRepository.findByConferenceIdAndId(conferenceId, sessionId);
    }
}
