package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Session;

import java.util.List;

public interface ConferenceQueryService {

    List<ConferenceResponse> getAllConferences();

    ConferenceDetailResponse getConference(Long conferenceId);

    List<Session> getSessionsByConferenceId(Long conferenceId);

    boolean isSessionReservable(Long sessionId);

    boolean areSessionsReservable(Long conferenceId, List<Long> sessionId);
}
