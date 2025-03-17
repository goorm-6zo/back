package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
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
    public Boolean existsByConferenceId(Long conferenceId) {
        return reservationJpaRepository.existsReservationByConferenceId(conferenceId);
    }

    @Override
    public Boolean existsByConferenceIdAndNameAndPhone(Long conferenceId, String name, String phone) {
        return reservationJpaRepository.existsByConferenceIdAndNameAndPhone(conferenceId, name, phone);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationJpaRepository.findById(id);
    }

    @Override
    public List<Reservation> findAllByUser(User user) { return reservationJpaRepository.findAllByUser(user); }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> findByNameAndPhone(String name, String phone) {
        return reservationJpaRepository.findByNameAndPhone(name, phone);
    }

    @Override
    public List<Reservation> findAllByNameAndPhone(String name, String phone) {
        return reservationJpaRepository.findAllByNameAndPhone(name, phone);
    }

    @Override
    public Optional<Reservation> findByPhoneAndConferenceId(String phone, Long conferenceId) {
        return reservationJpaRepository.findByPhoneAndConferenceId(phone, conferenceId);
    }

}
