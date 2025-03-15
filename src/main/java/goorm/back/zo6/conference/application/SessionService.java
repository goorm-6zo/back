package goorm.back.zo6.conference.application;

import java.time.LocalDateTime;

public interface SessionService {
    SessionResponse updateSession(Long sessionId, String name, Integer capacity, String location, LocalDateTime time, String summary);
}
