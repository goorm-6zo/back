package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {
    private Long reservationId;
    private ConferenceInfo conference;
    private List<SessionInfo> sessions;
    private ReservationStatus status;

    @Data
    @Builder
    public static class ConferenceInfo {
        private Long conferenceId;
        private String conferenceName;
        private String description;
        private String location;
        private LocalDateTime conferenceAt;
        private Integer capacity;
        private Boolean hasSessions;
    }

    @Data
    @Builder
    public static class SessionInfo {
        private Long sessionId;
        private Long conferenceId;
        private String sessionName;
        private Integer capacity;
        private String location;
        private LocalDateTime time;
        private String summary;
    }
}
