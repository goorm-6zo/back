package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.infrastructure.attendance.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "[Attendance] Attendance API", description = "Attendance API")
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "얼굴 인식 SSE 연결", description = "각 구역별 기기의 참석률 제공을 위해 SSE 연결을 시도합니다.")
    @GetMapping(value = "/subscribe",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(){
        return null;
    }
}
