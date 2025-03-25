package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponse {

    private Long id;

    private String name;

    private Integer capacity;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String summary;

    private String speakerName;

    private String speakerOrganization;

    private boolean isActive;

    private String speakerImage;

    private boolean isSpeaker;
}
