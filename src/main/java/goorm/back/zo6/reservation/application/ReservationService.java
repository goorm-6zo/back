package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.ConferenceSimpleResponse;

import java.util.List;

public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest reservationRequest);

    List<ReservationResponse> getMyReservations();

    ReservationResponse createTemporaryReservation(ReservationRequest reservationRequest);

    List<ConferenceSimpleResponse> getMyConferenceSimpleList();

    ReservationResponse getReservationDetailsById(Long reservationId);

    ReservationResponse linkReservationByPhone(String inputPhone);

    ReservationConferenceDetailResponse getReservedConferenceDetails(Long conferenceId);
}
