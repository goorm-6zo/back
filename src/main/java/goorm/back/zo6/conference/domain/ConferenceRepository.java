package goorm.back.zo6.conference.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {
    @EntityGraph(attributePaths = {"sessions"})
    Optional<Conference> findWithSessionsById(Long id);
}
