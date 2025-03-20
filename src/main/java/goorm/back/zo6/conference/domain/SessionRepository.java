package goorm.back.zo6.conference.domain;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> findByConferenceId(Long conferenceId);

    Optional<Session> findById(Long id);

    List<Session> findByConferenceIdAndIsActiveTrue(Long conferenceId);

    Session save(Session session);

    Optional<Session> findByConferenceIdAndSessionId(Long conferenceId, Long sessionId);
}
