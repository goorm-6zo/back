package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @EntityGraph(attributePaths = {"reservationSessions"})
    List<Reservation> findAllByNameAndPhone(String name, String phone);

    @EntityGraph(attributePaths = {"reservationSessions"})
    List<Reservation> findAllByPhoneAndStatus(String phone, ReservationStatus status);

    @EntityGraph(attributePaths = {"reservationSessions"})
    List<Reservation> findAllByUser(User user);

    @EntityGraph(attributePaths = {"reservationSessions"})
    List<Reservation> findByConferenceIdAndUserId(Long conferenceId, Long userId);

}
