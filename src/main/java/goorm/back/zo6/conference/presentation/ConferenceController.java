package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.*;
import goorm.back.zo6.conference.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceQueryService conferenceQueryService;

    @GetMapping
    public ResponseEntity<List<ConferenceResponse>> getAllConferences() {
        List<ConferenceResponse> conferences = conferenceQueryService.getAllConferences();
        return ResponseEntity.ok(conferences);
    }

    @GetMapping("/{conferenceId}")
    public ResponseEntity<ConferenceDetailResponse> getConference(@PathVariable Long conferenceId) {
        ConferenceDetailResponse response = conferenceQueryService.getConference(conferenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conferenceId}/sessions")
    public ResponseEntity<List<SessionResponse>> getSessionsByConferenceId(@PathVariable Long conferenceId) {
        return ResponseEntity.ok(conferenceQueryService.getSessionsByConferenceId(conferenceId));
    }

    @GetMapping("/{conferenceId}/sessions/{sessionId}")
    public ResponseEntity<Session> getSessionDetail(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        Session session = conferenceQueryService.getSessionDetail(conferenceId, sessionId);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/sessions/{sessionId}/reservable")
    public ResponseEntity<Boolean> isSessionReservable(@PathVariable Long sessionId) {
        Boolean reservable = conferenceQueryService.isSessionReservable(sessionId);
        return ResponseEntity.ok(reservable);
    }

    @GetMapping("/{conferenceId}/sessions/reservable")
    public ResponseEntity<Boolean> areSessionsReservable(@PathVariable Long conferenceId, @PathVariable List<Long> sessionId) {
        boolean allReservable = conferenceQueryService.areSessionsReservable(conferenceId, sessionId);
        return ResponseEntity.ok(allReservable);
    }
}