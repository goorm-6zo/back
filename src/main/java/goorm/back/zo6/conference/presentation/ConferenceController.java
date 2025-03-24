package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.dto.ConferenceDetailResponse;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.query.ConferenceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "conference", description = "Conference API")
@RestController
@RequestMapping("/api/v1/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceQueryService conferenceQueryService;

    @GetMapping
    @Operation(summary = "모든 컨퍼런스 조회", description = "등록된 모든 컨퍼런스 목록을 조회합니다.")
    public ResponseEntity<List<ConferenceResponse>> getAllConferences() {
        List<ConferenceResponse> conferences = conferenceQueryService.getAllConferences();
        return ResponseEntity.ok(conferences);
    }

    @GetMapping("/{conferenceId}")
    @Operation(summary = "특정 컨퍼런스 조회", description = "conferenceId로 특정 컨퍼런스의 상세 정보를 조회합니다.")
    public ResponseEntity<ConferenceDetailResponse> getConference(@PathVariable Long conferenceId) {
        ConferenceDetailResponse response = conferenceQueryService.getConference(conferenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conferenceId}/sessions/{sessionId}")
    @Operation(summary = "특정 세션 상세 조회", description = "conferenceId와 sessionId로 특정 세션의 상세 정보를 조회합니다.")
    public ResponseEntity<SessionDto> getSessionDetail(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        SessionDto session = conferenceQueryService.getSessionDetail(conferenceId, sessionId);
        return ResponseEntity.ok(session);
    }
}