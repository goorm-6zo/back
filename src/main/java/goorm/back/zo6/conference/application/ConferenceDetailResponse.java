package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceDetailResponse {

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

    private List<SessionDto> sessions;

    public static ConferenceDetailResponse fromEntity(Conference conference) {
        return new ConferenceDetailResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getConferenceAt(),
                conference.getCapacity(),
                conference.getImageKey(),
                conference.getIsActive(),
                conference.getHasSessions(),
                conference.getSessions().stream()
                        .map(SessionDto::fromEntity)
                        .toList()
        );
    }
}
