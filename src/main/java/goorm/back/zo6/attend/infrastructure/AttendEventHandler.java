package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendEventHandler {
    private final AttendRedisService attendRedisService;
    private final AttendService attendService;

    @EventListener(AttendEvent.class)
    public void handle(AttendEvent event){
        boolean alreadyAttend = attendRedisService.saveUserAttendance(event.getConferenceId(), event.getSessionId(), event.getUserId(), event.getTimeStamp());
        if(!alreadyAttend) {
            attendService.registerAttend(event.getUserId(), event.getConferenceId(), event.getSessionId());
        }
    }
}
