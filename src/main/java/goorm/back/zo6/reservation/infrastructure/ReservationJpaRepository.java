package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByNameAndPhone(String name, String phone);

    List<Reservation> findAllByPhoneAndStatus(String phone, ReservationStatus status);

    List<Reservation> findAllByUser(User user);

    List<Reservation> findByConferenceIdAndUserId(Long conferenceId, Long userId);

    boolean existsByUserIdAndConferenceIdAndStatus(Long userId, Long conferenceId, ReservationStatus status);

    boolean existsByUserIdAndConferenceIdAndReservationSessionsSessionIdAndStatus(
            Long userId, Long conferenceId, Long sessionId, ReservationStatus status);

}
