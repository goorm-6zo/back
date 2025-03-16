package goorm.back.zo6.attend.dto;

public record AttendResponse(
        Long id,
        Long reservationId,
        Long reservationSessionId
) {
}
