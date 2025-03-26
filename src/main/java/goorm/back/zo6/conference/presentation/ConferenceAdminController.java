package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.common.dto.ResponseDto;
import goorm.back.zo6.conference.application.command.conference.ConferenceCommandService;
import goorm.back.zo6.conference.application.command.session.SessionCommandService;
import goorm.back.zo6.conference.application.dto.*;
import goorm.back.zo6.conference.application.query.ConferenceQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "admin", description = "Admin Conference API")
@RestController
@RequestMapping("/api/v1/admin/conference")
@RequiredArgsConstructor
public class ConferenceAdminController {

    private final ConferenceQueryService conferenceQueryService;

    private final ConferenceCommandService conferenceCommandService;

    private final SessionCommandService sessionCommandService;

    @GetMapping
    @Operation(summary = "모든 컨퍼런스 조회", description = "등록된 모든 컨퍼런스 목록을 조회합니다. (관리자 전용)")
    public ResponseEntity<ResponseDto<List<ConferenceResponse>>> getAllConferences() {
        List<ConferenceResponse> conferences = conferenceQueryService.getAllConferences();
        return ResponseEntity.ok(ResponseDto.of(conferences));
    }

    @GetMapping("/{conferenceId}")
    @Operation(summary = "특정 컨퍼런스 조회", description = "conferenceId로 특정 컨퍼런스의 상세 정보를 조회합니다. (관리자 전용)")
    public ResponseEntity<ResponseDto<ConferenceResponse>> getConference(@PathVariable Long conferenceId) {
        ConferenceResponse response = conferenceQueryService.getConference(conferenceId);
        return ResponseEntity.ok(ResponseDto.of(response));
    }

    @GetMapping("/{conferenceId}/sessions/{sessionId}")
    @Operation(summary = "특정 세션 상세 조회", description = "conferenceId와 sessionId로 특정 세션의 상세 정보를 조회합니다. (관리자 전용)")
    public ResponseEntity<ResponseDto<SessionDto>> getSessionDetail(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        SessionDto session = conferenceQueryService.getSessionDetail(conferenceId, sessionId);
        return ResponseEntity.ok(ResponseDto.of(session));
    }

    @PutMapping("/{conferenceId}/status")
    @Operation(summary = "컨퍼런스 상태 변경", description = "활성화 / 비활성화를 변경합니다. (관리자 전용)")
    public ResponseEntity<ResponseDto<String>> conferenceStatus(@PathVariable Long conferenceId) {
        boolean currentStatus = conferenceCommandService.getConferenceStatus(conferenceId);
        boolean newStatus = !currentStatus;
        conferenceCommandService.updateConferenceStatus(conferenceId, newStatus);
        return ResponseEntity.ok(ResponseDto.of("상태가 변경되었습니다."));
    }

    @PutMapping("/{conferenceId}/sessions/{sessionId}")
    @Operation(summary = "세션 상태 변경", description = "활성화 / 비활성화를 변경합니다. (관리자 전용)")
    public ResponseEntity<ResponseDto<String>> sessionStatus(@PathVariable Long conferenceId, @PathVariable Long sessionId) {
        boolean currentStatus = sessionCommandService.getSessionStatus(conferenceId, sessionId);
        boolean newStatus = !currentStatus;
        sessionCommandService.updateSessionStatus(conferenceId, sessionId, newStatus);
        return ResponseEntity.ok(ResponseDto.of("상태가 변경되었습니다."));
    }

    @PutMapping("/sessions/{sessionId}")
    @Operation(summary = "세션 정보 수정", description = "세션의 정보를 수정합니다 (구역만 수정 가능).")
    public ResponseEntity<ResponseDto<SessionDto>> updateSession(@PathVariable Long sessionId, @Valid @RequestBody SessionUpdateRequest request) {
        SessionDto response = sessionCommandService.updateSession(
                sessionId,
                request.location()
        );
        return ResponseEntity.status(HttpStatus.OK).body(ResponseDto.of(response));
    }
}
