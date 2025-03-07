package goorm.back.zo6.conference.infrastructure;

import goorm.back.zo6.conference.domain.Conference;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceJpaRepository extends JpaRepository<Conference, Long> {
    @EntityGraph(attributePaths = {"sessions"})
    Optional<Conference> findWithSessionsById(Long id);
}
