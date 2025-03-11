package goorm.back.zo6.conference.infrastructure.attendance.sse;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Log4j2
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(String eventId, SseEmitter sseEmitter){
        emitters.put(eventId, sseEmitter);
        log.info("SSE 연결 저장 - eventId={}", eventId);
        return sseEmitter;
    }
}
