package goorm.back.zo6.conference.domain;

import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(Long id);

    Session save(Session session);
}
