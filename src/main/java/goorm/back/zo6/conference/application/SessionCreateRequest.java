package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {

    private Long conferenceId;

    private String name;

    private Integer capacity;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;

    private String summary;

    private String speakerName;

    private String speakerOrganization;

    private String speakerImage;
}
