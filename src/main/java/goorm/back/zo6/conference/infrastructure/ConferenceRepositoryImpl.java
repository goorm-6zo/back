package goorm.back.zo6.conference.infrastructure;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConferenceRepositoryImpl implements ConferenceRepository {

    private final ConferenceJpaRepository conferenceJpaRepository;

    @Override
    public Optional<Conference> findWithSessionsById(Long conferenceId) {
        return conferenceJpaRepository.findWithSessionsById(conferenceId);
    }

    @Override
    public List<Conference> findAll() {
        return conferenceJpaRepository.findAll();
    }
}
