package goorm.back.zo6.conference.application;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceSimpleResponse {
    private Long conferenceId;
    private String conferenceName;
    private LocalDateTime conferenceAt;
    private String conferenceLocation;
}
