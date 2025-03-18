package goorm.back.zo6.notice.presntation;

import goorm.back.zo6.notice.application.NoticeService;
import goorm.back.zo6.notice.dto.NoticeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notices", description = "알림 전송 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("/{conferenceId}/session/{sessionId}")
    @Operation(summary = "알림 전송", description = "알림 대상을 ALL, ATTENDEE, NON_ATTENDEE 중 하나로 보내주세요.")
    public ResponseEntity<String> sendNotice(@PathVariable Long conferenceId, @PathVariable(required = false) Long sessionId, @RequestBody NoticeRequest noticeRequest){
        noticeService.sendMessage(noticeRequest.message(),conferenceId,sessionId,noticeRequest.noticeTarget());
        return ResponseEntity.ok("메시지 전송 완료");
    }

}
