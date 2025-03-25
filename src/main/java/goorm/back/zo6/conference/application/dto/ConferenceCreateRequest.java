package goorm.back.zo6.conference.application.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ConferenceCreateRequest (
    String name,
    String description,
    String location,
    Integer capacity,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
    String imageUrl,
    Boolean isActive,
    Boolean hasSessions
) {}
