package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.ConferenceSimpleResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest reservationRequest);

    List<ReservationResponse> getMyReservations();

    ReservationResponse createTemporaryReservation(ReservationRequest reservationRequest);

    List<Long> getMyConferenceIds();

    List<ConferenceSimpleResponse> getMyConferenceSimpleList();

    ReservationResponse getReservationDetailsById(Long reservationId);

    ReservationResponse linkReservationByPhoneAndUser(String inputPhone, Long userId);

    void confirmUserReservationsAfterLogin(String name, String phone);
}
