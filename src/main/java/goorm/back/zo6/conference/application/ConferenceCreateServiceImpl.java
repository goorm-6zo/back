package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConferenceCreateServiceImpl implements ConferenceCreateService {

    private final ConferenceRepository conferenceRepository;

    @Override
    public ConferenceResponse createConference(ConferenceCreateRequest request) {
        Conference conference = Conference.builder()
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .conferenceAt(request.getConferenceAt())
                .imageKey(request.getImageUrl())
                .isActive(true)
                .hasSessions(request.getHasSessions())
                .build();

        Conference savedConference = conferenceRepository.save(conference);

        return new ConferenceResponse(
                savedConference.getId(),
                savedConference.getName(),
                savedConference.getDescription(),
                savedConference.getLocation(),
                savedConference.getConferenceAt(),
                savedConference.getCapacity(),
                savedConference.getImageKey(),
                savedConference.getIsActive(),
                savedConference.getHasSessions()
        );
    }
}
