package goorm.back.zo6.reservation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);

    Reservation save(Reservation reservation);

    Boolean existsByConferenceId(Long conferenceId);

    Boolean existsByConferenceIdAndNameAndPhone(Long conferenceId, String name, String phone);

    Optional<Reservation> findByNameAndPhone(String name, String phone);

}

