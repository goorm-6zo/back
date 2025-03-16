package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.face.infrastructure.event.AttendanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendEventHandler {
    private final AttendRedisService attendRedisService;
    @EventListener(AttendanceEvent.class)
    public void handle(AttendanceEvent event){
        attendRedisService.saveUserAttendance(event.getConferenceId(), event.getSessionId(), event.getUserId(), event.getTimeStamp());
    }
}
