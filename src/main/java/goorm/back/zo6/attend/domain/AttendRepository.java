package goorm.back.zo6.attend.domain;

import jakarta.persistence.Tuple;

import java.util.List;

public interface AttendRepository {
    Attend save(Attend attend);
    void deleteByUserIdAndReservationIdAndReservationSessionId(Long userId, Long reservationId, Long reservationSession);
    List<Attend> findByUserId(Long userId);

    List<Attend> findByUserIdAndReservationId(Long userId, Long reservationId);

    List<Tuple> findAttendInfoByUserAndConference(Long userId, Long conferenceId);
}
