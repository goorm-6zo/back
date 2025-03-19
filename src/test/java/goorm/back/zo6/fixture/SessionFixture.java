package goorm.back.zo6.fixture;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationSession;

import java.time.LocalDateTime;

public class SessionFixture {

    public static Session 세션(Conference conference) {
        return Session.builder()
                .conference(conference)
                .name("테스트 세션")
                .capacity(100)
                .location("온라인")
                .time(LocalDateTime.now())
                .summary("테스트 세션 요약")
                .build();
    }

    public static ReservationSession 예약_세션(Reservation reservation, Session session) {
        return ReservationSession.builder()
                .reservation(reservation)
                .session(session)
                .build();
    }

    public static Session 세션_아이디포함(Conference conference) {
        return Session.builder()
                .id(1L)
                .name("테스트 세션")
                .conference(conference)
                .capacity(100)
                .location("온라인")
                .time(LocalDateTime.now())
                .summary("테스트 세션 요약")
                .build();
    }
}