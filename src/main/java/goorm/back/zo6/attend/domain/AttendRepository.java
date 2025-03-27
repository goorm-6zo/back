package goorm.back.zo6.attend.domain;

import goorm.back.zo6.attend.dto.AttendDataDto;
import jakarta.persistence.Tuple;

import java.util.List;

public interface AttendRepository {
    Attend save(Attend attend);
    List<Tuple> findAttendInfoByUserAndConference(Long userId, Long conferenceId);
    List<Tuple> findAttendData(String phone, Long conferenceId, Long sessionId);
    List<Tuple> findUsersWithAttendanceAndMeta(Long conferenceId, Long sessionId);
    AttendDataDto findAttendInfo(String phone, Long conferenceId, Long sessionId);
}
