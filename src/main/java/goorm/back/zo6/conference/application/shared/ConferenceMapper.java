package goorm.back.zo6.conference.application.shared;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
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
        return ConferenceResponse.from(conference, generateConferenceImageUrl(conference.getImageKey()));
    }

    public ConferenceResponse toConferenceDetailResponse(Conference conference) {
        List<SessionDto> sortedSessions = conference.getSessions().stream()
                .sorted(Comparator.comparing(Session::getStartTime))
                .map(this::toSessionDto)
                .toList();

        return ConferenceResponse.detailFrom(conference, generateConferenceImageUrl(conference.getImageKey()), sortedSessions);
    }

    public ConferenceResponse toConferenceSimpleResponse(Conference conference) {
        return ConferenceResponse.simpleFrom(conference, generateConferenceImageUrl(conference.getImageKey()));
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

    private String generateConferenceImageUrl(String imageKey) {
        if (imageKey == null) {
            return null;
        }
        return s3FileService.generatePresignedUrl(imageKey, 60);
    }

    private String generateSpeakerImageUrl(String speakerImageKey) {
        if (speakerImageKey == null) {
            return null;
        }
        return s3FileService.generatePresignedUrl(speakerImageKey, 60);
    }
}
