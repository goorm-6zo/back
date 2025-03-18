package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttendInfoResponse {
    private ConferenceInfoDto conference; // 컨퍼런스 정보
    private SessionInfoDto session; // 세션 정보 (없을 수도 있음)
}
