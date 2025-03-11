package goorm.back.zo6.reservation.application;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest reservationRequest);

    List<ReservationResponse> getMyReservations();

}
