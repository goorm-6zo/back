package goorm.back.zo6.conference.application;

import java.time.LocalDateTime;

public interface ConferenceCreateService {
    ConferenceResponse createConference(ConferenceCreateRequest request);
}
