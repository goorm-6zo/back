package goorm.back.zo6.reservation.application;

import goorm.back.zo6.reservation.domain.ReservationStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReservationResponse (
    Long reservationId,
    ConferenceInfo conference,
    List<SessionInfo> sessions,
    ReservationStatus status
) {
    @Builder
    public record ConferenceInfo(
            Long conferenceId,
            String conferenceName,
            String description,
            String location,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            Integer capacity,
            Boolean hasSessions,
            String imageUrl
    ) {}

    @Builder
    public record SessionInfo(
            Long sessionId,
            Long conferenceId,
            String sessionName,
            Integer capacity,
            String location,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            String summary,
            String speaker,
            String speakerOrganization,
            String imageUrl
    ) {}
}
