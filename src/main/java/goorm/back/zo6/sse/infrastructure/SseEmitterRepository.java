package goorm.back.zo6.sse.infrastructure;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String eventKey, SseEmitter sseEmitter) {
        emitters.put(eventKey, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void deleteByEventKey(String eventKey) {
        emitters.remove(eventKey);
    }

    @Override
    public SseEmitter findEmitterByKey(String eventKey) {
        return emitters.get(eventKey);
    }
}
