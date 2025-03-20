package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.domain.AttendEvent;
import goorm.back.zo6.attend.dto.AttendInfo;
import goorm.back.zo6.sse.application.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AttendEventHandler {
    private final AttendRedisService attendRedisService;
    private final AttendService attendService;
    private final SseService sseService;

    @Async("customTaskExecutor")
    @EventListener(AttendEvent.class)
    public void handle(AttendEvent event){
        Long conferenceId = event.getConferenceId();
        Long sessionId = event.getSessionId();
        Long userId = event.getUserId();

        log.info("참석 이벤트 리스너 클래스 - 메서드들 호출 전 : {}", Thread.currentThread().getName());
        AttendInfo attendInfo = attendRedisService.saveUserAttendance(conferenceId, sessionId, userId, event.getTimeStamp());
        sseService.sendAttendanceCount(conferenceId, sessionId, attendInfo.attendCount());

        if(!attendInfo.alreadyAttended()) {
            attendService.registerAttend(userId, conferenceId, sessionId);
        }
        log.info("참석 이벤트 리스너 클래스 - 메서드들 호출 후 : {}", Thread.currentThread().getName());
    }
}
