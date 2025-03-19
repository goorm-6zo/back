package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.domain.AttendEvent;
import goorm.back.zo6.attend.dto.AttendInfo;
import goorm.back.zo6.sse.application.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendEventHandler {
    private final AttendRedisService attendRedisService;
    private final AttendService attendService;
    private final SseService sseService;

    @EventListener(AttendEvent.class)
    public void handle(AttendEvent event){
        Long conferenceId = event.getConferenceId();
        Long sessionId = event.getSessionId();
        Long userId = event.getUserId();

        AttendInfo attendInfo = attendRedisService.saveUserAttendance(conferenceId, sessionId, userId, event.getTimeStamp());
        sseService.sendAttendanceCount(conferenceId, sessionId, attendInfo.attendCount());

        if(!attendInfo.alreadyAttended()) {
            attendService.registerAttend(userId, conferenceId, sessionId);
        }
    }
}
