package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Session;

import java.util.List;

public interface ConferenceQueryService {

    List<ConferenceResponse> getAllConferences();

    ConferenceDetailResponse getConference(Long conferenceId);

    List<SessionResponse> getSessionsByConferenceId(Long conferenceId);

    List<SessionDto> getSessionsByConferenceIdDto(Long conferenceId);

    Session getSessionDetail(Long conferenceId, Long sessionId);

    boolean getSessionStatus(Long conferenceId, Long sessionId);

    void updateSessionStatus(Long conferenceId, Long sessionId, boolean newStatus);

    boolean getConferenceStatus(Long conferenceId);

    void updateConferenceStatus(Long conferenceId, boolean newStatus);
}
