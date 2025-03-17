package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.*;
import goorm.back.zo6.conference.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{conferenceId}/sessions/{sessionId}")
    public ResponseEntity<SessionDto> getSessionDetail(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        Session session = conferenceQueryService.getSessionDetail(conferenceId, sessionId);
        return ResponseEntity.ok(SessionDto.fromEntity(session));
    }
}