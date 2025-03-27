package goorm.back.zo6.attend.domain;

import goorm.back.zo6.attend.dto.AttendData;
import goorm.back.zo6.attend.dto.AttendanceSummaryQuery;
import goorm.back.zo6.attend.dto.ConferenceInfoResponse;

public interface AttendRepository {
    Attend save(Attend attend);
    AttendData findAttendInfo(String phone, Long conferenceId, Long sessionId);
    ConferenceInfoResponse findAttendInfoByUserAndConference(Long userId, Long conferenceId);
    AttendanceSummaryQuery findAttendanceSummary(Long conferenceId, Long sessionId);
}
