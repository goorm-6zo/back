package goorm.back.zo6.conference.infrastructure.attendance;

import goorm.back.zo6.face.infrastructure.event.AttendanceEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AttendanceEventHandler {
    private final AttendanceService attendanceService;
    @EventListener(AttendanceEvent.class)
    public void handle(AttendanceEvent event){
        attendanceService.saveUserAttendance(event.getConferenceId(), event.getSessionId(), event.getUserId(), event.getTimeStamp());
    }
}
