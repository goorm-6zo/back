package goorm.back.zo6.reservation.application.shared;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReservationFactory {

    public Reservation createReservationEntity(Conference conference, ReservationRequest reservationRequest, Set<Session> sessions, ReservationStatus status) {
        Reservation reservation = Reservation.builder()
                .conference(conference)
                .name(reservationRequest.getName())
                .phone(reservationRequest.getPhone())
                .status(status)
                .build();

        sessions.forEach(reservation::addSession);
        return reservation;
    }
}
