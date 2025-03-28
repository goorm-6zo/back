package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findAllByUserId(Long id) {
        return reservationJpaRepository.findAllByUserId(id);
    }

    @Override
    public List<Reservation> findAllByUser(User user) { return reservationJpaRepository.findAllByUser(user); }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> findAllByPhoneAndStatus(String phone, ReservationStatus status) {
        return reservationJpaRepository.findAllByPhoneAndStatus(phone, status);
    }

    @Override
    public List<Reservation> findAllByNameAndPhone(String name, String phone) {
        return reservationJpaRepository.findAllByNameAndPhone(name, phone);
    }

    @Override
    public List<Reservation> findByConferenceIdAndUserId(Long conferenceId, Long userId) {
        return reservationJpaRepository.findByConferenceIdAndUserId(conferenceId, userId);
    }

    @Override
    public Boolean existsByUserIdAndConferenceId(Long userId, Long conferenceId) {
        return reservationJpaRepository.existsByUserIdAndConferenceIdAndStatus(userId, conferenceId, ReservationStatus.CONFIRMED);
    }

    @Override
    public Boolean existsByUserAndConferenceAndSession(Long userId, Long conferenceId, Long sessionId) {
        return reservationJpaRepository.existsByUserIdAndConferenceIdAndReservationSessionsSessionIdAndStatus(userId, conferenceId, sessionId, ReservationStatus.CONFIRMED);
    }
}
