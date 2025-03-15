package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ConferenceDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime conferenceAt;
    private Integer capacity;
    private Boolean hasSessions;
    private List<SessionDto> sessions;

    public static ConferenceDetailResponse fromEntity(Conference conference) {
        return new ConferenceDetailResponse(conference.getId(), conference.getName(), conference.getDescription(), conference.getLocation(), conference.getConferenceAt(), conference.getCapacity(), conference.getHasSessions(), conference.getSessions().stream().map(SessionDto::fromEntity).toList()
        );
    }
}
