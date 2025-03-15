package goorm.back.zo6.sse.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
    SseEmitter save(String eventKey, SseEmitter sseEmitter);
    void deleteByEventKey(String eventKey);
    SseEmitter findEmitterByKey(String key);
}
