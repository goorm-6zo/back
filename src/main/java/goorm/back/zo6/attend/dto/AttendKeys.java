package goorm.back.zo6.attend.dto;

import lombok.Builder;

@Builder
public record AttendKeys(
        String attendanceKey,
        String countKey,
        boolean isSession
) {
}
