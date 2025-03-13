package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private Conference conferenceId;
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime time;
    private String summary;
}