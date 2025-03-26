package goorm.back.zo6.reservation.application;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationRequest (
    Long conferenceId,
    List<Long> sessionIds,
    String name,
    String phone
) {}
