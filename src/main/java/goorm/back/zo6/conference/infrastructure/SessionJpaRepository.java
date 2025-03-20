package goorm.back.zo6.conference.infrastructure;

import goorm.back.zo6.conference.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionJpaRepository extends JpaRepository<Session, Long> {
    List<Session> findByConferenceId(Long conferenceId);

    List<Session> findByConferenceIdAndIsActiveTrue(Long conferenceId);

    Optional<Session> findByConferenceIdAndId(Long conferenceId, Long sessionId);
}
