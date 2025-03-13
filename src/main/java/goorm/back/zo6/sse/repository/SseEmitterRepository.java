package goorm.back.zo6.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public void deleteAllEmittersByKey(String eventKey) {
        String userIdPrefix = eventKey +"_";
        emitters.forEach((k, v) ->{
            if(k.startsWith(userIdPrefix)) emitters.remove(k);
        });
    }
    @Override
    public Map<String, SseEmitter> findAllEmitters() {
        return emitters;
    }
    @Override
    public SseEmitter findEmitterByKey(String eventKey) {
        return emitters.get(eventKey);
    }

    @Override
    public Map<String, SseEmitter> findEmittersByKey(Set<String> eventKeys) {
        return emitters.entrySet().stream()
                .filter(entry ->{
                    String key = entry.getKey();
                    return eventKeys.stream().anyMatch(id -> key.startsWith(id+"_"));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
