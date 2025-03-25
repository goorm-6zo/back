package goorm.back.zo6.conference.application.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record SessionCreateRequest (
    Long conferenceId,
    String name,
    Integer capacity,
    String location,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
    String summary,
    String speakerName,
    String speakerOrganization,
    String speakerImage
) {}
