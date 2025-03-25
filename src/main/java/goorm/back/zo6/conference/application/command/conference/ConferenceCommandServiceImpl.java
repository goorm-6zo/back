package goorm.back.zo6.conference.application.command.conference;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.application.dto.ConferenceCreateRequest;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConferenceCommandServiceImpl implements ConferenceCommandService {

    private final ConferenceRepository conferenceRepository;

    @Override
    public boolean getConferenceStatus(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
        return conference.getIsActive();
    }

    @Override
    public void updateConferenceStatus(Long conferenceId, boolean newStatus) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
        conference.updateActive(newStatus);
        conferenceRepository.save(conference);
    }

    @Override
    public ConferenceResponse createConference(ConferenceCreateRequest request) {
        Conference conference = Conference.builder()
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .imageKey(request.getImageUrl())
                .isActive(true)
                .hasSessions(request.getHasSessions())
                .build();

        Conference savedConference = conferenceRepository.save(conference);

        return ConferenceResponse.from(savedConference, conference.getImageKey());
    }
}
