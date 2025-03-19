package goorm.back.zo6.sse.service;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.sse.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Log4j2
public class SseService {
    private final EmitterRepository emitterRepository;

    private static final long TIMEOUT = 1800*1000L;
    private static final long RECONNECTION_TIMEOUT = 1000L;

    public SseEmitter subscribe(Long conferenceId, Long sessionId){
        String eventKey = generateEventKey(conferenceId, sessionId);
        SseEmitter sseEmitter = emitterRepository.save(eventKey, new SseEmitter(TIMEOUT));

        registerEmitterHandler(eventKey, sseEmitter);

        sendToClient(eventKey, sseEmitter, "알림 구독 성공 [key = " + eventKey + "]");

        return sseEmitter;
    }

    // SseEmitter를 통해 클라이언트에게 이벤트를 전송하는 역할을 합니다.
    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data){
        SseEmitter.SseEventBuilder event = getSseEvent(eventId, data);
        try{
            sseEmitter.send(event);
        }catch (IOException e){
            log.error("구독 실패, eventId ={}, {}", eventId, e.getMessage());
        }
    }

    // 이벤트 id와, data를 이용해서 SSE 이벤트 객체를 생성합니다.
    private SseEmitter.SseEventBuilder getSseEvent(String eventId, Object data){
        return SseEmitter.event()
                .id(eventId)
                .data(data)
                .reconnectTime(RECONNECTION_TIMEOUT);  // 클라이언트와 연결이 끊겼을 때 클라이언트가 서버와 재연결을 시도하기 전에 대기할 시간
        // 해당 기능은 라이언트가 재연결 시도를 할 수 있도록 브라우저에게 힌트를 주는 역할
    }

    // 기기에 해당하는 참석자 수 리턴
    public void sendAttendanceCount(Long conferenceId, Long sessionId, long count){
        String eventKey = generateEventKey(conferenceId, sessionId);
        SseEmitter emitter = emitterRepository.findEmitterByKey(eventKey);
        if(emitter != null){
            try{
                emitter.send(SseEmitter.event()
                        .name("AttendanceCount")
                        .id(eventKey)
                        .reconnectTime(RECONNECTION_TIMEOUT)
                        .data(count));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.SSE_CONNECTION_FAILED);
            }
        }
    }

    // SSE 연결이 종료되거나, 타임아웃되거나, 오류가 발생할 때 적절한 처리를 수행하도록 Emitter에 핸들러를 등록
    void registerEmitterHandler(String eventId, SseEmitter sseEmitter){
        /*  SSE 연결이 정상적으로 종료되었을 때
           - 사용자가 브라우저를 닫거나 SSE 구독을 취소할 때
           - 서버에서 sseEmitter.complete()을 호출했을 때
        */
        sseEmitter.onCompletion(()->{
            log.info("연결이 끝났습니다. : eventId = {}", eventId);
            emitterRepository.deleteByEventKey(eventId);
        });

        /*  SSE 연결이 타임아웃되었을 때
           - 클라이언트가 네트워크 문제로 오랜 시간 동안 응답을 받지 못했을 때
           - 서버에서 일정 시간 동안 데이터가 전송되지 않아 클라이언트가 반응하지 않을 때
           - SseEmitter가 설정된 timeout이 초과되었을 때
        */
        sseEmitter.onTimeout(()->{
            log.info("Timeout이 발생했습니다. : eventId={}", eventId);
            emitterRepository.deleteByEventKey(eventId);
        });

        /*  SSE 연결 중 에러가 발생했을 때
            - 클라이언트가 갑자기 연결을 끊었을 때 (ERR_INCOMPLETE_CHUNKED_ENCODING)
            - 서버에서 SSE 메시지를 전송하는 중 네트워크 장애가 발생했을 때
            - 잘못된 데이터 형식이 전송되어 클라이언트가 파싱하지 못할 때
         */
        sseEmitter.onError((e) ->{
            log.info("에러가 발생했습니다. error={}, eventId={}", e.getMessage(),eventId);
            emitterRepository.deleteByEventKey(eventId);
        });
    }

    private String generateEventKey(Long conferenceId, Long sessionId){
        if(conferenceId == null){
            throw new CustomException(ErrorCode.MISSING_REQUIRED_PARAMETER);
        }

        if(sessionId == null){
            return "conference:" + conferenceId;
        }else{
            return "conference:" + conferenceId + ":session:" + sessionId;
        }
    }
}
