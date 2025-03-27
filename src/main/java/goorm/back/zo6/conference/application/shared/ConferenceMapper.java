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
        String imageUrl = generateConferenceImageUrl(conference.getImageKey());
        return ConferenceResponse.from(conference, imageUrl);
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
        return SessionDto.from(session, generateSpeakerImageUrl(session.getSpeakerImageKey()));
    }

    private String generateConferenceImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return s3FileService.generatePresignedUrl(imageKey, 60);
    }

    private String generateSpeakerImageUrl(String speakerImageKey) {
        if (speakerImageKey == null || speakerImageKey.isBlank()) {
            return null;
        }
        return s3FileService.generatePresignedUrl(speakerImageKey, 60);
    }
}
