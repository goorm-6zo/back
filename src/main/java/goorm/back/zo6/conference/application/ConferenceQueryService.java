package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
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

    public List<ConferenceResponse> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public ConferenceDetailResponse getConference(Long id) {
        Conference conference = conferenceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Conference not found"));
        return new ConferenceDetailResponse(
                conference.getId(),
                conference.getName(),
                conference.getHasSessions(),
                conference.getSessions().stream()
                        .map(session -> new SessionResponse(
                                session.getId(),
                                session.getName(),
                                session.getCapacity(),
                                session.getLocation(),
                                session.getTime(),
                                session.getSummary()
                        )).toList()
        );
    }

    public List<SessionResponse> getSessionByConferenceId(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId).orElseThrow(() -> new IllegalArgumentException("Conference not found"));
        return conference.getSessions().stream()
                .map(session -> new SessionResponse(
                        session.getId(),
                        session.getName(),
                        session.getCapacity(),
                        session.getLocation(),
                        session.getTime(),
                        session.getSummary()
                )).toList();
    }

    private ConferenceResponse convertToDto(Conference conference) {
        return new ConferenceResponse(
                conference.getId(),
                conference.getName(),
                conference.getHasSessions()
        );
    }
}
