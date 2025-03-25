package goorm.back.zo6.conference.application.dto;

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
    String speakerImage
) {}