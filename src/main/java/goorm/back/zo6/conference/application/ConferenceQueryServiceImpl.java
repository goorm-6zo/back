package goorm.back.zo6.conference.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
class ConferenceQueryServiceImpl implements ConferenceQueryService {

    private final ConferenceRepository conferenceRepository;

    private final ConferenceMapper conferenceMapper;

    private final SessionRepository sessionRepository;

    @Override
    public List<ConferenceResponse> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(conferenceMapper::toConferenceResponse)
                .toList();
    }

    @Override
    public ConferenceDetailResponse getConference(Long conferenceId) {
        Conference conference = findConferenceOrThrow(conferenceId);

        List<Session> sortedSessions = conference.getSessions().stream()
                .sorted(Comparator.comparing(Session::getStartTime)).toList();

        return conferenceMapper.toConferenceDetailResponse(conference);
    }

    @Override
    public List<SessionResponse> getSessionsByConferenceId(Long conferenceId) {
        Conference conference = findConferenceOrThrow(conferenceId);
        if (!conference.getHasSessions()) {
            throw new CustomException(ErrorCode.CONFERENCE_HAS_NO_SESSION);
        }

        return conference.getSessions().stream()
                .sorted(Comparator.comparing(Session::getStartTime))
                .map(conferenceMapper::toSessionResponse)
                .toList();
    }

    @Override
    public List<SessionDto> getSessionsByConferenceIdDto(Long conferenceId) {
        Conference conference = findConferenceOrThrow(conferenceId);

        if (!conference.getHasSessions()) {
            throw new CustomException(ErrorCode.CONFERENCE_HAS_NO_SESSION);
        }

        return conference.getSessions().stream()
                .map(conferenceMapper::toSessionDto)
                .toList();
    }

    public Session getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
    }

    @Override
    public Session getSessionDetail(Long conferenceId, Long sessionId) {
        Conference conference = findConferenceOrThrow(conferenceId);

        if (!conference.containsSession(sessionId)) {
            throw new CustomException(ErrorCode.SESSION_NOT_BELONG_TO_CONFERENCE);
        }

        return getSessionOrThrow(sessionId);
    }

    @Override
    public boolean getSessionStatus(Long conferenceId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
        return session.isActive();
    }

    @Override
    public void updateSessionStatus(Long conferenceId, Long sessionId, boolean newStatus) {
        Session session = sessionRepository.findByConferenceIdAndSessionId(conferenceId, sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUNT));
        session.updateActive(newStatus);
        sessionRepository.save(session);
    }

    @Override
    public boolean getConferenceStatus(Long conferenceId) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
        return conference.getIsActive();
    }

    @Override
    public void updateConferenceStatus(Long conferenceId, boolean newStatus) {
        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
        conference.updateActive(newStatus);
        conferenceRepository.save(conference);
    }

    private Conference findConferenceOrThrow(Long conferenceId) {
        return conferenceRepository.findWithSessionsById(conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));
    }

    private Session getSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
    }
}
