package goorm.back.zo6.conference.application.command.conference;

import goorm.back.zo6.conference.application.dto.ConferenceCreateRequest;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;

public interface ConferenceCommandService {

    void updateConferenceStatus(Long conferenceId, boolean newStatus);

    boolean getConferenceStatus(Long conferenceId);

    ConferenceResponse createConference(ConferenceCreateRequest request);
}
