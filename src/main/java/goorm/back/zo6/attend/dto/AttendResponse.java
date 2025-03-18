package goorm.back.zo6.attend.dto;

import goorm.back.zo6.attend.domain.Attend;
import lombok.Builder;

@Builder
public record AttendResponse(
        Long id,
        Long reservationId,
        Long reservationSessionId
) {
    public static AttendResponse from(Attend attend){
        return AttendResponse.builder()
                .id(attend.getId())
                .reservationId(attend.getReservationId())
                .reservationSessionId(attend.getReservationSessionId())
                .build();
    }
}
