package goorm.back.zo6.reservation.application;

import goorm.back.zo6.reservation.domain.ReservationStatus;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

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

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        private Integer capacity;

        private Boolean hasSessions;

        private String imageUrl;
    }

    @Data
    @Builder
    public static class SessionInfo {

        private Long sessionId;

        private Long conferenceId;

        private String sessionName;

        private Integer capacity;

        private String location;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        private String summary;

        private String speaker;

        private String speakerOrganization;

        private String imageUrl;
    }
}
