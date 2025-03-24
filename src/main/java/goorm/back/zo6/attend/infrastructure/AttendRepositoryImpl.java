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
    public List<Tuple> findAttendInfoByUserAndConference(Long userId, Long conferenceId) {
        return attendJpaRepository.findAttendInfoByUserAndConference(userId,conferenceId);
    }

    @Override
    public List<Tuple> findAttendData(String phone, Long conferenceId, Long sessionId) {
        return attendJpaRepository.findAttendData(phone, conferenceId, sessionId);
    }
}
