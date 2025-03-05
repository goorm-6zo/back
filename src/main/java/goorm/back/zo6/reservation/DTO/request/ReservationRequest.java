package goorm.back.zo6.reservation.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReservationRequest {
    private Long conferenceId;

    private List<Long> sessionIds;

    private String name;

    private String phone;
}
