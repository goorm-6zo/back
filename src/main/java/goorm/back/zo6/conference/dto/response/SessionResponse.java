package goorm.back.zo6.conference.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime time;
    private String summary;
}
