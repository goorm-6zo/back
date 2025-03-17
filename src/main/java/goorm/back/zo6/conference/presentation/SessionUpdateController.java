package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.SessionResponse;
import goorm.back.zo6.conference.application.SessionService;
import goorm.back.zo6.conference.application.SessionUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "session", description = "Session API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionUpdateController {

    private final SessionService sessionService;

    @PutMapping("/{sessionId}")
    @Operation(summary = "세션 정보 수정", description = "세션의 정보를 수정합니다.")
    public ResponseEntity<SessionResponse> updateSession(@PathVariable Long sessionId, @Valid @RequestBody SessionUpdateRequest request) {
        SessionResponse response = sessionService.updateSession(
                sessionId,
                request.getName(),
                request.getCapacity(),
                request.getLocation(),
                request.getTime(),
                request.getSummary()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
