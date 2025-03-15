package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConferenceCreateRequest {
    private String name;
    private String description;
    private String location;
    private Integer capacity;
    private LocalDateTime conferenceAt;
    private Boolean hasSessions;
}
