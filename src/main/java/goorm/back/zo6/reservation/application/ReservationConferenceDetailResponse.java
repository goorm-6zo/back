package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.SessionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private LocalDateTime conferenceAt;
    private String conferenceDescription;
    private List<SessionDto> sessions;
}
