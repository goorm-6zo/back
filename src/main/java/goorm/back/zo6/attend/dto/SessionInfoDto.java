package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SessionInfoDto {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String summary;
    private String speakerName;
    private String speakerOrganization;
    private boolean isActive;
    private boolean isAttend;
}