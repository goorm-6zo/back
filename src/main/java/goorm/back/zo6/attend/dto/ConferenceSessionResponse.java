package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceSessionResponse {
    private ConferenceInfoDto conference;
    private SessionInfoDto session;
}
