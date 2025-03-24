package goorm.back.zo6.conference.application.dto;

import goorm.back.zo6.conference.domain.Conference;
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

    public static ConferenceResponse fromEntity(Conference conference) {
        return new ConferenceResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getStartTime(),
                conference.getEndTime(),
                conference.getCapacity(),
                conference.getImageKey(),
                conference.getIsActive(),
                conference.getHasSessions()
        );
    }
}
