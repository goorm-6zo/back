package goorm.back.zo6.attend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AttendanceSummaryResponse(
        String name,
        Integer capacity,
        long attendedCount,
        List<UserAttendanceResponse> userAttendances
) {
    public static AttendanceSummaryResponse of(String name,Integer capacity, long attendedCount, List<UserAttendanceResponse> userAttendances){
        return AttendanceSummaryResponse.builder()
                .name(name)
                .capacity(capacity)
                .attendedCount(attendedCount)
                .userAttendances(userAttendances)
                .build();
    }
}
