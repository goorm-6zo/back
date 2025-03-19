package goorm.back.zo6.attend.domain;

import goorm.back.zo6.common.event.Event;
import lombok.Getter;


@Getter
public class AttendEvent extends Event {
    private Long userId;
    private Long conferenceId;
    private Long sessionId;

    public AttendEvent(Long userId, Long conferenceId, Long sessionId) {
        super();
        this.userId = userId;
        this.conferenceId = conferenceId;
        this.sessionId = sessionId;
    }
}
