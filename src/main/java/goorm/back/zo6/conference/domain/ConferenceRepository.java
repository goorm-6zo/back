package goorm.back.zo6.conference.domain;

import java.util.List;
import java.util.Optional;

public interface ConferenceRepository {
    Optional<Conference> findWithSessionsById(Long conferenceId);
    List<Conference> findAll();
}
