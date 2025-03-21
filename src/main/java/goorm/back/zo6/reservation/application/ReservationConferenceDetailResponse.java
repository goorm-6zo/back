package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.SessionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationConferenceDetailResponse {

    private Long conferenceId;

    private String conferenceName;

    private String conferenceLocation;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String conferenceDescription;

    private List<SessionDto> sessions;
}
