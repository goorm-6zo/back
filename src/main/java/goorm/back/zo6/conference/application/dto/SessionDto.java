package goorm.back.zo6.conference.application.dto;

import goorm.back.zo6.conference.domain.Session;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
public record SessionDto (
    Long id,
    Long conferenceId,
    String name,
    Integer capacity,
    String location,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
    String summary,
    String speakerName,
    String speakerOrganization,
    boolean isActive,
    String speakerImage,
    boolean speakerStatus
) {
    public static SessionDto from(Session session, String speakerImage) {
        return SessionDto.builder()
                .id(session.getId())
                .conferenceId(session.getConference().getId())
                .name(session.getName())
                .capacity(session.getCapacity())
                .location(session.getLocation())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .summary(session.getSummary())
                .speakerName(validSpeakerName(session.getSpeakerName()))
                .speakerOrganization(session.getSpeakerOrganization())
                .isActive(session.isActive())
                .speakerImage(speakerImage)
                .speakerStatus(session.getSpeakerName() != null && !session.getSpeakerName().isBlank())
                .build();
    }

    private static String validSpeakerName(String speakerName) {
        return (speakerName != null && !speakerName.isBlank()) ? speakerName : null;
    }
}