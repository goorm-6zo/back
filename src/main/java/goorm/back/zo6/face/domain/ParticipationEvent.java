package goorm.back.zo6.face.domain;

import goorm.back.zo6.common.event.Event;

public class ParticipationEvent extends Event {
    private Long userId;
    private Long conferenceId;
    private Long sessionId;
}
