package goorm.back.zo6.conference.application.query;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.application.dto.ConferenceDetailResponse;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.shared.ConferenceMapper;
import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.application.shared.SessionValidator;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConferenceQueryServiceImpl implements ConferenceQueryService {

    private final ConferenceRepository conferenceRepository;

    private final ConferenceMapper conferenceMapper;

    private final ConferenceValidator conferenceValidator;

    private final SessionValidator sessionValidator;

    @Override
    public List<ConferenceResponse> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(conferenceMapper::toConferenceResponse)
                .toList();
    }

    @Override
    public ConferenceDetailResponse getConference(Long conferenceId) {
        Conference conference = conferenceValidator.findConferenceOrThrow(conferenceId);
        return conferenceMapper.toConferenceDetailResponse(conference);
    }

    @Override
    public List<SessionDto> getSessionsByConferenceId(Long conferenceId) {
        Conference conference = conferenceValidator.findConferenceWithSessionsOrThrow(conferenceId);
        if (Boolean.FALSE.equals(conference.getHasSessions())) {
            throw new CustomException(ErrorCode.CONFERENCE_HAS_NO_SESSION);
        }

        return conference.getSessions().stream()
                .sorted(Comparator.comparing(Session::getStartTime))
                .map(conferenceMapper::toSessionDto)
                .toList();
    }

    @Override
    public SessionDto getSessionDetail(Long conferenceId, Long sessionId) {
        Conference conference = conferenceValidator.findConferenceOrThrow(conferenceId);
        if (Boolean.FALSE.equals(conference.containsSession(sessionId))) {
            throw new CustomException(ErrorCode.SESSION_NOT_BELONG_TO_CONFERENCE);
        }

        Session session = sessionValidator.getSessionOrThrow(sessionId);

        return conferenceMapper.toSessionDto(session);
    }
}
