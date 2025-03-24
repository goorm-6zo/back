package goorm.back.zo6.conference.application.shared;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConferenceValidator {
    private final ConferenceRepository conferenceRepository;

    public Conference findConferenceOrThrow(Long id) {
        return conferenceRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
    }

    public Conference findConferenceWithSessionsOrThrow(Long id) {
        return conferenceRepository.findWithSessionsById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
    }
}
