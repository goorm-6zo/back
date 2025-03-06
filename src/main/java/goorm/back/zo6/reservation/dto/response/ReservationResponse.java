package goorm.back.zo6.reservation.dto.response;

import lombok.Builder;

import java.util.List;

public class ReservationResponse {
    private boolean success;

    private String message;

    private List<Long> reservedSessionIds;

    private Long reservedConferenceId;

    @Builder
    public ReservationResponse(boolean success, String message, List<Long> reservedSessionIds, Long reservedConferenceId) {
        this.success = success;
        this.message = message;
        this.reservedSessionIds = reservedSessionIds;
        this.reservedConferenceId = reservedConferenceId;
    }
}
