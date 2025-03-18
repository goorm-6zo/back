package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceInfoDto {
    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime conferenceAt;
    private Integer capacity;
    private Boolean hasSessions;
    private boolean isAttend;
    private List<SessionInfoDto> sessions;
}
