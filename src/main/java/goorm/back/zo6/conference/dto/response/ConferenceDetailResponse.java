package goorm.back.zo6.conference.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ConferenceDetailResponse {
    private Long id;
    private String name;
    private Boolean hasSessions;
    private List<SessionResponse> sessions;
}
