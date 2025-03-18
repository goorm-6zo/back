package goorm.back.zo6.attend.dto;

import lombok.Builder;

@Builder
public record AttendInfo(
        boolean alreadyAttended,
        long attendCount
) {
    public static AttendInfo of(boolean alreadyAttended, long attendCount){
        return AttendInfo.builder()
                .alreadyAttended(alreadyAttended)
                .attendCount(attendCount)
                .build();
    }
}
