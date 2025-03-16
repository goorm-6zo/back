package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConferenceResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime conferenceAt;
    private Integer capacity;
    private Boolean hasSessions;
}
