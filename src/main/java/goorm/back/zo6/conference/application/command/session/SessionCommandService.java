package goorm.back.zo6.conference.application.command.session;

import goorm.back.zo6.conference.application.dto.SessionCreateRequest;
import goorm.back.zo6.conference.application.dto.SessionDto;

public interface SessionCommandService {
    void updateSessionStatus(Long conferenceId, Long sessionId, boolean newStatus);

    boolean getSessionStatus(Long conferenceId, Long sessionId);

    SessionDto updateSession(Long sessionId, String location);

    SessionDto createSession(SessionCreateRequest request);
}
