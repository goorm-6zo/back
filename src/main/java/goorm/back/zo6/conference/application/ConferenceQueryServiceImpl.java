package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        return conferenceMapper.toConferenceDetailResponse(conference);
    }

    @Override
    public List<Session> getSessionsByConferenceId(Long conferenceId) {
        Conference conference = findConferenceOrThrow(conferenceId);

        if (!conference.getHasSessions()) {
            throw new IllegalArgumentException("This conference does not have any sessions.");
        }

        return sessionRepository.findByConferenceId(conferenceId);
    }

    public Session getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> new IllegalArgumentException("Session not found."));
    }

    @Override
    public boolean isSessionReservable(Long sessionId) {
        Session session = getSessionById(sessionId);
        return session.isReservable();
    }

    @Override
    public boolean areSessionsReservable(Long conferenceId, List<Long> sessionIds) {
        List<Session> sessions = sessionRepository.findByConferenceId(conferenceId);
        return sessions.stream()
                .filter(session -> sessionIds.contains(session.getId())).
                allMatch(Session::isReservable);
    }

    private Conference findConferenceOrThrow(Long conferenceId) {
        return conferenceRepository.findWithSessionsById(conferenceId)
                .orElseThrow(() -> new IllegalArgumentException("Conference not found."));
    }

    private Session getSessionOrThrow(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));
    }
}
