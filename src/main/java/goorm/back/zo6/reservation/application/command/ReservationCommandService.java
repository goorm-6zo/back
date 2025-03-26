package goorm.back.zo6.reservation.application.command;

import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.application.ReservationResponse;

public interface ReservationCommandService {

    ReservationResponse createReservation(ReservationRequest reservationRequest);

    ReservationResponse createTemporaryReservation(ReservationRequest reservationRequest);

    ReservationResponse linkReservationByPhone(String inputPhone);
}
