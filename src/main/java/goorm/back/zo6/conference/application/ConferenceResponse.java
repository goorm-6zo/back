package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConferenceResponse {
    private Long id;
    private String name;
    private Boolean hasSessions;
}
