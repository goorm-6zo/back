package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.dto.response.ConferenceDetailResponse;
import goorm.back.zo6.conference.dto.response.ConferenceResponse;
import goorm.back.zo6.conference.dto.response.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConferenceQueryService {

    private final ConferenceRepository conferenceRepository;
    private final ConferenceMapper conferenceMapper;

    public List<ConferenceResponse> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(conferenceMapper::toConferenceResponse)
                .toList();
    }

    public ConferenceDetailResponse getConference(Long id) {
        Conference conference = findConferenceOrThrow(id);
        return conferenceMapper.toConferenceDetailResponse(conference);
    }

    public List<SessionResponse> getSessionByConferenceId(Long conferenceId) {
        Conference conference = findConferenceOrThrow(conferenceId);

        if (!conference.getHasSessions()) {
            throw new IllegalArgumentException("This conference does not have any sessions.");
        }

        return conference.getSessions().stream()
                .map(conferenceMapper::toSessionResponse)
                .toList();
    }

    private Conference findConferenceOrThrow(Long id) {
        return conferenceRepository.findWithSessionsById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found."));
    }
}
