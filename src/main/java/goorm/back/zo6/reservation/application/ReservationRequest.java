package goorm.back.zo6.reservation.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequest {
    private Long conferenceId;

    private List<Long> sessionIds;

    private String name;

    private String phone;
}
