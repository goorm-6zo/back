package goorm.back.zo6.sse.presentation;

import goorm.back.zo6.sse.application.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "sse", description = "Sse API")
@RestController
@RequestMapping("/api/v1/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @Operation(summary = "얼굴 인식 SSE 연결", description = "각 구역별 기기의 참석률 제공을 위해 SSE 연결을 시도합니다.")
    @GetMapping(value = "/subscribe",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam("conferenceId") Long conferenceId,
                                @RequestParam(value = "sessionId", required = false) Long sessionId){
        return sseService.subscribe(conferenceId, sessionId);
    }

    @DeleteMapping("/unsubscribe")
    @Operation(summary = "얼굴 인식 SSE 연결", description = "각 구역별 기기의 참석률 제공을 위해 SSE 연결을 시도합니다.")
    public ResponseEntity<Void> unsubscribe(@RequestParam("conferenceId") Long conferenceId,
                                            @RequestParam(value = "sessionId", required = false) Long sessionId) {
        sseService.unsubscribe(conferenceId,sessionId);
        return ResponseEntity.ok().build();
    }
}