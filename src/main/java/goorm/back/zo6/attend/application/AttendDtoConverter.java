package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.dto.ConferenceInfoResponse;
import goorm.back.zo6.attend.dto.SessionInfo;
import goorm.back.zo6.conference.infrastructure.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AttendDtoConverter {

    private final S3FileService s3FileService;

    public ConferenceInfoResponse convertConferenceInfoResponse(ConferenceInfoResponse original) {
        String presignedConferenceImageUrl = s3FileService.generatePresignedUrl(original.getImageUrl(), 60);

        List<SessionInfo> sessionsWithUrl = null;

        if (original.getSessions() != null) {
            sessionsWithUrl = original.getSessions().stream()
                    .map(this::convertSessionInfo)
                    .collect(Collectors.toList());
        }

        return new ConferenceInfoResponse(
                original.getId(),
                original.getName(),
                original.getDescription(),
                original.getLocation(),
                original.getStartTime(),
                original.getEndTime(),
                original.getCapacity(),
                original.getHasSessions(),
                presignedConferenceImageUrl,
                original.getIsActive(),
                original.isAttend(),
                sessionsWithUrl
        );
    }

    private SessionInfo convertSessionInfo(SessionInfo original) {
        String speakerImagePresignedUrl = s3FileService.generatePresignedUrl(original.getSpeakerImageKey(), 60);

        return new SessionInfo(
                original.getId(),
                original.getName(),
                original.getCapacity(),
                original.getLocation(),
                original.getStartTime(),
                original.getEndTime(),
                original.getSummary(),
                original.getSpeakerName(),
                original.getSpeakerOrganization(),
                speakerImagePresignedUrl,
                original.isActive(),
                original.isAttend()
        );
    }
}
