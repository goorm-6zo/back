package goorm.back.zo6.conference.application.query;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;

import java.util.List;

public interface ConferenceQueryService {

    List<ConferenceResponse> getAllConferences();

    ConferenceResponse getConference(Long conferenceId);

    List<SessionDto> getSessionsByConferenceId(Long conferenceId);

    SessionDto getSessionDetail(Long conferenceId, Long sessionId);
}
