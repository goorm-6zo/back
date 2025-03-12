package goorm.back.zo6.conference.application;

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
    private List<SessionResponse> sessions;
}
