package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttendDataDto {
    private Long reservationId;
    private Long reservationSessionId;
    private Long conferenceId;
    private Long sessionId;
}
