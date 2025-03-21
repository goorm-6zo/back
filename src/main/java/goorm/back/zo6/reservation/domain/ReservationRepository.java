package goorm.back.zo6.reservation.domain;

import goorm.back.zo6.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Optional<Reservation> findById(Long id);

    List<Reservation> findAllByUser(User user);

    Reservation save(Reservation reservation);

    List<Reservation> findAllByPhoneAndStatus(String phone, ReservationStatus status);

    List<Reservation> findAllByNameAndPhone(String name, String phone);

    List<Reservation> findByConferenceIdAndUserId(Long conferenceId, Long userId);

    boolean existsByUserIdAndConferenceId(Long userId, Long conferenceId);

    boolean existsByUserAndConferenceAndSession(Long userId, Long conferenceId, Long sessionId);
}

