package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.ConferenceDetailResponse;
import goorm.back.zo6.conference.application.ConferenceQueryService;
import goorm.back.zo6.conference.application.ConferenceResponse;
import goorm.back.zo6.conference.application.SessionDto;
import goorm.back.zo6.conference.domain.Session;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "admin", description = "Admin Conference API")
@RestController
@RequestMapping("/api/v1/admin/conference")
@RequiredArgsConstructor
public class ConferenceAdminController {
    private final ConferenceQueryService conferenceQueryService;

    @GetMapping
    @Operation(summary = "모든 컨퍼런스 조회", description = "등록된 모든 컨퍼런스 목록을 조회합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ConferenceResponse>> getAllConferences() {
        List<ConferenceResponse> conferences = conferenceQueryService.getAllConferences();
        return ResponseEntity.ok(conferences);
    }

    @GetMapping("/{conferenceId}")
    @Operation(summary = "특정 컨퍼런스 조회", description = "conferenceId로 특정 컨퍼런스의 상세 정보를 조회합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConferenceDetailResponse> getConference(@PathVariable Long conferenceId) {
        ConferenceDetailResponse response = conferenceQueryService.getConference(conferenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conferenceId}/sessions/{sessionId}")
    @Operation(summary = "특정 세션 상세 조회", description = "conferenceId와 sessionId로 특정 세션의 상세 정보를 조회합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionDto> getSessionDetail(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        Session session = conferenceQueryService.getSessionDetail(conferenceId, sessionId);
        return ResponseEntity.ok(SessionDto.fromEntity(session));
    }

    @PutMapping("/{conferenceId}/sessions/{sessionId}")
    @Operation(summary = "세션 상태 변경", description = "활성화 / 비활성화를 변경합니다. (관리자 전용)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sessionStatus(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        boolean currentStatus = conferenceQueryService.getSessionStatus(conferenceId, sessionId);
        boolean newStatus = !currentStatus;
        conferenceQueryService.updateSessionStatus(conferenceId, sessionId, newStatus);
        return ResponseEntity.ok("상태가 변경되었습니다.");
    }
}
