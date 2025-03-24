package goorm.back.zo6.fixture;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;

import java.util.List;

public class ReservationFixture {

    public static Reservation 확정된예약(Conference conference, List<Session> sessions, String name, String phone, User user) {
        Reservation reservation = Reservation.builder()
                .conference(conference)
                .name(name)
                .phone(phone)
                .status(ReservationStatus.CONFIRMED)
                .user(user)
                .build();

        sessions.forEach(reservation::addSession);

        return reservation;
    }
}
