package goorm.back.zo6.conference.infrastructure;

import goorm.back.zo6.conference.domain.Conference;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceJpaRepository extends JpaRepository<Conference, Long> {
    @EntityGraph(attributePaths = {"sessions"})
    @Query("""
        SELECT DISTINCT c FROM Conference c
        LEFT JOIN FETCH c.sessions s
        WHERE c.id = :conferenceId
        ORDER BY s.startTime ASC
    """)
    Optional<Conference> findWithSessionsById(@Param("conferenceId") Long conferenceId);
}
