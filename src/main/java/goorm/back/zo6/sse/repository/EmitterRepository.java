package goorm.back.zo6.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;

public interface EmitterRepository {
    SseEmitter save(String eventKey, SseEmitter sseEmitter);
    void deleteByEventKey(String eventKey);
    void deleteAllEmittersByKey(String key);
    Map<String, SseEmitter> findAllEmitters();
    SseEmitter findEmitterByKey(String key);
    Map<String, SseEmitter> findEmittersByKey(Set<String> keys);

}
