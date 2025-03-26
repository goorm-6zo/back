package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.common.dto.ResponseDto;
import goorm.back.zo6.conference.application.command.session.SessionCommandService;
import goorm.back.zo6.conference.application.dto.SessionCreateRequest;
import goorm.back.zo6.conference.application.dto.SessionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "session", description = "Session API")
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionCreateController {

    private final SessionCommandService sessionCommandService;

    @PostMapping
    @Operation(summary = "세션 생성", description = "새로운 세션을 생성합니다.")
    public ResponseEntity<ResponseDto<SessionDto>> createSession(@Valid @RequestBody SessionCreateRequest request) {
        SessionDto session = sessionCommandService.createSession(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(session));
    }
}
