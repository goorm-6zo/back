package goorm.back.zo6.attend.dto;

import lombok.Builder;

@Builder
public record UserAttendanceResponse(
        Long userId,
        String userName,
        boolean isAttended
){
    public static UserAttendanceResponse of(Long userId, String userName, boolean isAttended){
        return UserAttendanceResponse.builder()
                .userId(userId)
                .userName(userName)
                .isAttended(isAttended)
                .build();
    }
}
