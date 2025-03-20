package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionUpdateRequest {
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime time;
    private String summary;
    private String speakerName;
    private String speakerOrganization;
    private boolean isActive;
}
