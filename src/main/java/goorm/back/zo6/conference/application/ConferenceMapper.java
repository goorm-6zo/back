package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import org.springframework.stereotype.Component;

@Component
public class ConferenceMapper {
    public ConferenceResponse toConferenceResponse(Conference conference) {
        return new ConferenceResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getConferenceAt(),
                conference.getCapacity(),
                conference.getHasSessions()
        );
    }

    public ConferenceDetailResponse toConferenceDetailResponse(Conference conference) {
        return new ConferenceDetailResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getConferenceAt(),
                conference.getCapacity(),
                conference.getHasSessions(),
                conference.getSessions().stream()
                        .map(this::toSessionDto).toList()
        );
    }

    public SessionResponse toSessionResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getTime(),
                session.getSummary()
        );
    }

    public SessionDto toSessionDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getConference().getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getTime(),
                session.getSummary()
        );
    }
}
