package goorm.back.zo6.conference.application.dto;

import goorm.back.zo6.conference.domain.Conference;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Builder
public record ConferenceResponse (
    Long id,
    String name,
    String description,
    String location,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
    Integer capacity,
    String imageUrl,
    Boolean isActive,
    Boolean hasSessions,
    List<SessionDto> sessions
) {
    public static ConferenceResponse detailFrom(Conference conference, String imageUrl, List<SessionDto> sessions) {
        return ConferenceResponse.builder()
                .id(conference.getId())
                .name(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .capacity(conference.getCapacity())
                .imageUrl(imageUrl)
                .isActive(conference.getIsActive())
                .hasSessions(conference.getHasSessions())
                .sessions(sessions)
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
                .imageUrl(imageUrl)
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
                .imageUrl(imageUrl)
                .build();
    }
}
