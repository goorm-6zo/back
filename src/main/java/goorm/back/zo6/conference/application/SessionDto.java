package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionDto {
    private Long id;

    private Long conferenceId;

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

    private boolean speakerStatus;

    public static SessionDto fromEntity(Session session) {
        return new SessionDto(
                session.getId(),
                session.getConference() != null ? session.getConference().getId() : null,
                session.getName(), session.getCapacity(),
                session.getLocation(),
                session.getStartTime(),
                session.getEndTime(),
                session.getSummary(),
                session.getSpeakerName(),
                session.getSpeakerOrganization(),
                session.isActive(),
                session.getSpeakerImageKey(),
                session.getSpeakerName() != null && !session.getSpeakerName().isBlank()
        );
    }
}