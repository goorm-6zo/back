package goorm.back.zo6.reservation.application;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
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
