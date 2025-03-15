package goorm.back.zo6.sse.dto;

import lombok.Builder;

@Builder
public record AttendanceKeys(
        String attendanceKey,
        String countKey,
        boolean isSession
) {
}
