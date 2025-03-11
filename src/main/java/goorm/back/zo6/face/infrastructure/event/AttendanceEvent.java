package goorm.back.zo6.face.infrastructure.event;

import goorm.back.zo6.common.event.Event;
import lombok.Getter;


@Getter
public class AttendanceEvent extends Event {
    private Long userId;
    private Long conferenceId;
    private Long sessionId;

    public AttendanceEvent(Long userId, Long conferenceId, Long sessionId) {
        super();
        this.userId = userId;
        this.conferenceId = conferenceId;
        this.sessionId = sessionId;
    }
}
