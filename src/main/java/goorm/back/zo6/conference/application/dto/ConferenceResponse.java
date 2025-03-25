package goorm.back.zo6.conference.application.dto;

import goorm.back.zo6.conference.domain.Conference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConferenceResponse {

    private Long id;

    private String name;

    private String description;

    private String location;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer capacity;

    private String imageUrl;

    private Boolean isActive;

    private Boolean hasSessions;

    @Builder.Default
    private List<SessionDto> sessions = Collections.emptyList();

    public static ConferenceResponse detailFrom(Conference conference, String imageUrl, List<SessionDto> sessions) {
        return ConferenceResponse.builder()
                .id(conference.getId())
                .name(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .capacity(conference.getCapacity())
                .imageUrl(conference.getImageKey())
                .isActive(conference.getIsActive())
                .hasSessions(conference.getHasSessions())
                .sessions(
                        conference.getSessions().stream()
                                .map(SessionDto::fromEntity)
                                .toList()
                )
                .build();
    }

    public static ConferenceResponse from(Conference conference, String imageUrl) {
        return ConferenceResponse.builder()
                .id(conference.getId())
                .name(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .capacity(conference.getCapacity())
                .imageUrl(conference.getImageKey())
                .isActive(conference.getIsActive())
                .hasSessions(conference.getHasSessions())
                .sessions(Collections.emptyList())
                .build();
    }

    public static ConferenceResponse simpleFrom(Conference conference, String imageUrl) {
        return ConferenceResponse.builder()
                .id(conference.getId())
                .name(conference.getName())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .imageUrl(conference.getImageKey())
                .build();
    }
}
