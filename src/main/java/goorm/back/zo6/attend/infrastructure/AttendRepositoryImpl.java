package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AttendRepositoryImpl implements AttendRepository {
    private final AttendJpaRepository attendJpaRepository;

    @Override
    public Attend save(Attend attend) {
        return attendJpaRepository.save(attend);
    }

    @Override
    public void deleteByUserIdAndReservationIdAndReservationSessionId(Long userId, Long reservationId, Long reservationSessionId) {
        attendJpaRepository.deleteByUserIdAndReservationIdAndReservationSessionId(userId, reservationId, reservationSessionId);
    }
    @Override
    public List<Attend> findByUserId(Long userId) {
        return null;
    }

    @Override
    public List<Attend> findByUserIdAndReservationId(Long userId, Long reservationId) {
        return attendJpaRepository.findByUserIdAndConferenceId(userId,reservationId);
    }

    @Override
    public List<Tuple> findAttendInfoByUserAndConference(Long userId, Long conferenceId) {
        return attendJpaRepository.findAttendInfoByUserAndConference(userId,conferenceId);
    }
}
