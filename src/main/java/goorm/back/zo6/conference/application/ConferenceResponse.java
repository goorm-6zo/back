package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConferenceResponse {

    private Long id;

    private String name;

    private String description;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime conferenceAt;

    private Integer capacity;

    private String imageUrl;

    private Boolean isActive;

    private Boolean hasSessions;
}
