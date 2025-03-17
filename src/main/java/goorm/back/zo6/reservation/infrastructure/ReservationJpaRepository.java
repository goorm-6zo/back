package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    Boolean existsReservationByConferenceId(Long conferenceId);

    Boolean existsByConferenceIdAndNameAndPhone(Long conferenceId, String name, String phone);

    Optional<Reservation> findByNameAndPhone(String name, String phone);

    List<Reservation> findAllByNameAndPhone(String name, String phone);

    Optional<Reservation> findByPhoneAndConferenceId(String phone, Long conferenceId);

    List<Reservation> findAllByUser(User user);

}
