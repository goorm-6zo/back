package goorm.back.zo6.attend.dto;

import lombok.Builder;

@Builder
public record AttendInfo(
        boolean isNewUser,
        long attendCount
) {
    public static AttendInfo of(boolean isNewUser, long attendCount){
        return AttendInfo.builder()
                .isNewUser(isNewUser)
                .attendCount(attendCount)
                .build();
    }
}
