package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionCreateRequest {
    private Long conferenceId;
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime time;
    private String summary;
}
