package goorm.back.zo6.conference.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionCreateServiceImpl implements SessionCreateService {

    private final ConferenceRepository conferenceRepository;

    private final SessionRepository sessionRepository;

    @Override
    @Transactional
    public SessionResponse createSession(SessionCreateRequest request) {
        Conference conference = conferenceRepository.findById(request.getConferenceId()).orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
        Session session = Session.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .summary(request.getSummary())
                .speakerName(request.getSpeakerName())
                .speakerOrganization(request.getSpeakerOrganization())
                .isActive(true)
                .speakerImageKey(request.getSpeakerImage())
                .conference(conference)
                .build();

        Session savedSession = sessionRepository.save(session);

        return new SessionResponse(
                savedSession.getId(),
                savedSession.getName(),
                savedSession.getCapacity(),
                savedSession.getLocation(),
                savedSession.getStartTime(),
                savedSession.getEndTime(),
                savedSession.getSummary(),
                savedSession.getSpeakerName(),
                savedSession.getSpeakerOrganization(),
                savedSession.isActive(),
                savedSession.getSpeakerImageKey(),
                session.getSpeakerName() != null && !session.getSpeakerName().isBlank());
    }
}
