package goorm.back.zo6.reservation.application.query;

import goorm.back.zo6.conference.application.dto.ConferenceSimpleResponse;
import goorm.back.zo6.reservation.application.ReservationConferenceDetailResponse;
import goorm.back.zo6.reservation.application.ReservationResponse;

import java.util.List;

public interface ReservationQueryService {

    List<ReservationResponse> getMyReservations();

    List<ConferenceSimpleResponse> getMyConferenceSimpleList();

    ReservationResponse getReservationDetailsById(Long reservationId);

    ReservationConferenceDetailResponse getReservedConferenceDetails(Long conferenceId);
}
