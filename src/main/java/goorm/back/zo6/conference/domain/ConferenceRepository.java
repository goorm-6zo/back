package goorm.back.zo6.conference.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConferenceRepository {

    @EntityGraph(attributePaths = {"sessions"})
    Optional<Conference> findById(Long id);
    @Query("SELECT c FROM Conference c LEFT JOIN FETCH c.sessions WHERE c.id = :conferenceId")
    Optional<Conference> findWithSessionsById(@Param("conferenceId") Long conferenceId);
    List<Conference> findAll();
    Conference save(Conference conference);
}
