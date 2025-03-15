package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.SessionResponse;
import goorm.back.zo6.conference.application.SessionService;
import goorm.back.zo6.conference.application.SessionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionUpdateController {

    private final SessionService sessionService;

    @PutMapping("/{sessionId}")
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
