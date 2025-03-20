package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceCreateRequest {

    private String name;

    private String description;

    private String location;

    private Integer capacity;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime conferenceAt;

    private String imageUrl;

    private Boolean isActive;

    private Boolean hasSessions;
}
