package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SessionDto {
    private Long id;
    private Long conferenceId;
    private String name;
    private Integer capacity;
    private String location;
    private LocalDateTime time;
    private String summary;

    public static SessionDto fromEntity(Session session) {
        return new SessionDto(
                session.getId(),
                session.getConference() != null ? session.getConference().getId() : null,
                session.getName(), session.getCapacity(),
                session.getLocation(),
                session.getTime(),
                session.getSummary());
    }
}