package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConferenceMapper {

    private final S3FileService s3FileService;

    public ConferenceResponse toConferenceResponse(Conference conference) {
        return new ConferenceResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getStartTime(),
                conference.getEndTime(),
                conference.getCapacity(),
                s3FileService.generatePresignedUrl(conference.getImageKey(), 60),
                conference.getIsActive(),
                conference.getHasSessions()
        );
    }

    public ConferenceDetailResponse toConferenceDetailResponse(Conference conference) {
        List<SessionDto> sortedSessions = conference.getSessions().stream()
                .sorted(Comparator.comparing(Session::getStartTime))
                .map(this::toSessionDto)
                .toList();

        return new ConferenceDetailResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getStartTime(),
                conference.getEndTime(),
                conference.getCapacity(),
                s3FileService.generatePresignedUrl(conference.getImageKey(), 60),
                conference.getIsActive(),
                conference.getHasSessions(),
                sortedSessions
        );
    }

    public SessionResponse toSessionResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getStartTime(),
                session.getEndTime(),
                session.getSummary(),
                session.getSpeakerName(),
                session.getSpeakerOrganization(),
                session.isActive(),
                generateSpeakerImageUrl(session.getSpeakerImageKey())
        );
    }

    public SessionDto toSessionDto(Session session) {
        return new SessionDto(
                session.getId(),
                session.getConference().getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getStartTime(),
                session.getEndTime(),
                session.getSummary(),
                session.getSpeakerName(),
                session.getSpeakerOrganization(),
                session.isActive(),
                generateSpeakerImageUrl(session.getSpeakerImageKey())
        );
    }

    private String generateSpeakerImageUrl(String speakerImageKey) {
        if (speakerImageKey == null) {
            return null;
        }
        return s3FileService.generatePresignedUrl(speakerImageKey, 60);
    }
}
