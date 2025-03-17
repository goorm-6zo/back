package goorm.back.zo6.face.dto.request;

public record ParticipationRequest(
        Long userId,
        Long conferenceId,
        Long sessionId
) {
}
