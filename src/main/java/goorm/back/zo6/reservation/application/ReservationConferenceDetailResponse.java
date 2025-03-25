package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.dto.SessionDto;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReservationConferenceDetailResponse (
    Long conferenceId,
    String conferenceName,
    String conferenceLocation,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
    String conferenceDescription,
    List<SessionDto> sessions
) {}
