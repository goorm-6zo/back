package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceInfoResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private Boolean hasSessions;
    private String imageUrl;
    private Boolean isActive;
    private boolean isAttend;
    private List<SessionInfo> sessions;
}
