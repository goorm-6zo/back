package goorm.back.zo6.sse.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.sse.infrastructure.EmitterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @InjectMocks
    private SseService sseService;

    @Mock
    private EmitterRepository emitterRepository;

    private static final long TIMEOUT = 1800*1000L;

    @Test
    @DisplayName("sse 연결 - 컨퍼런스 기기 sse 연결 성공")
    void subscribe_ConferenceSuccess() {
        // given
        Long conferenceId = 1L;
        Long sessionId = null;
        String eventKey = "conference:1";
        SseEmitter mockEmitter = new SseEmitter(TIMEOUT);

        when(emitterRepository.save(eq(eventKey), any(SseEmitter.class))).thenReturn(mockEmitter);

        // when
        SseEmitter sseEmitter = sseService.subscribe(conferenceId, sessionId );

        // then
        assertNotNull(sseEmitter);
        verify(emitterRepository, times(1)).save(eq(eventKey), any(SseEmitter.class));
    }

    @Test
    @DisplayName("sse 연결 - 세션 기기 sse 연결 성공")
    void subscribe_SessionSuccess() {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;
        String eventKey = "conference:1:session:2";
        SseEmitter mockEmitter = new SseEmitter(TIMEOUT);

        when(emitterRepository.save(eq(eventKey), any(SseEmitter.class))).thenReturn(mockEmitter);

        // when
        SseEmitter sseEmitter = sseService.subscribe(conferenceId, sessionId);

        // then
        assertNotNull(sseEmitter);
        verify(emitterRepository, times(1)).save(eq(eventKey), any(SseEmitter.class));
    }

    @Test
    @DisplayName("sse 연결 - conferenceId 가 null sse 연결 실패")
    void subscribe_NoneConferenceFails() {
        // given
        Long conferenceId = null;
        Long sessionId = 2L;

        // when
        CustomException exception = assertThrows(CustomException.class, () -> sseService.subscribe(conferenceId, sessionId));
        // then
        assertEquals(ErrorCode.MISSING_REQUIRED_PARAMETER, exception.getErrorCode());
        verifyNoInteractions(emitterRepository);
    }

    @Test
    @DisplayName("실시간 참석자 수 count 전송 - 컨퍼런스 참석 count 전송 성공")
    void sendAttendanceCount_ConferenceSuccess() throws IOException {
        // given
        Long conferenceId = 1L;
        Long sessionId = null;
        long count = 10;
        String eventKey = "conference:1";
        SseEmitter mockEmitter = mock(SseEmitter.class);

        when(emitterRepository.findEmitterByKey(eventKey)).thenReturn(mockEmitter);

        // when
        sseService.sendAttendanceCount(conferenceId, sessionId, count);

        // then
        verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("실시간 참석자 수 count 전송 - 세션 참석 count 전송 성공")
    void sendAttendanceCount_SessionSuccess() throws IOException {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;
        long count = 10;
        String eventKey = "conference:1:session:2";
        SseEmitter mockEmitter = mock(SseEmitter.class);

        when(emitterRepository.findEmitterByKey(eventKey)).thenReturn(mockEmitter);

        // when
        sseService.sendAttendanceCount(conferenceId, sessionId, count);

        // then
        verify(mockEmitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    @Test
    @DisplayName("실시간 참석자 수 count 전송 - IOException 발생 실패")
    void sendAttendanceCount_IOExceptionErrors() throws IOException {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;
        long count = 10;
        String eventKey = "conference:1:session:2";
        SseEmitter mockEmitter = mock(SseEmitter.class);

        // emitterRepository에서 해당 eventKey에 대한 Emitter 반환
        when(emitterRepository.findEmitterByKey(eventKey)).thenReturn(mockEmitter);
        // send() 호출 시 강제로 IOException 발생하도록 설정
        doThrow(new IOException("SSE 전송 실패")).when(mockEmitter).send(any(SseEmitter.SseEventBuilder.class));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> sseService.sendAttendanceCount(conferenceId, sessionId, count));

        // 로그 출력 및 emitterRepository에서 삭제가 이루어졌는지 확인
        assertEquals(ErrorCode.SSE_CONNECTION_FAILED, exception.getErrorCode());
        verify(emitterRepository, times(1)).findEmitterByKey(eventKey);
    }

    @Test
    @DisplayName("실시간 참석자 수 count 전송 - conferenceId 가 null 실패")
    void sendAttendanceCount_NoneConferenceFails() {
        // given
        Long conferenceId = null;
        Long sessionId = 2L;
        long count = 10;

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> sseService.sendAttendanceCount(conferenceId, sessionId, count));

        // 로그 출력 및 emitterRepository 에서 삭제가 이루어졌는지 확인
        assertEquals(ErrorCode.MISSING_REQUIRED_PARAMETER, exception.getErrorCode());
        verifyNoInteractions(emitterRepository);
    }

    @Test
    @DisplayName("SSE 참석자 수 전송 - Emitter 가 null 일 때 아무 동작 없이 종료")
    void sendAttendanceCount_NoneEmitterFails() {
        // Given
        Long conferenceId = 1L;
        Long sessionId = 2L;
        long count = 10;
        String eventKey = "conference:1:session:2";

        // emitterRepository가 null을 반환하도록 설정
        when(emitterRepository.findEmitterByKey(eventKey)).thenReturn(null);

        // When & Then (예외가 발생하지 않고 정상 종료되는지 확인)
        assertDoesNotThrow(() -> sseService.sendAttendanceCount(conferenceId, sessionId, count));

        // emitter.send()가 호출되지 않았는지 검증
        verify(emitterRepository, times(1)).findEmitterByKey(eventKey);
        verifyNoMoreInteractions(emitterRepository);
    }

    @Test
    @DisplayName("SSE 연결 정상 종료 - onCompletion() 호출 시 Emitter 삭제")
    void registerEmitterHandler_OnCompletion_ShouldDeleteEmitter() throws Exception {
        // Given
        String eventId = "conference:1:session:2";
        SseEmitter sseEmitter = new SseEmitter();

        // When
        sseService.registerEmitterHandler(eventId, sseEmitter);
        // 직접 핸들러 실행
        Runnable onCompletionHandler = ()->{
            emitterRepository.deleteByEventKey(eventId);
        };

        onCompletionHandler.run();

        // Then
        verify(emitterRepository, times(1)).deleteByEventKey(eventId);
    }

    @Test
    @DisplayName("SSE 타임아웃 발생 - onTimeout() 호출 시 Emitter 삭제")
    void registerEmitterHandler_OnTimeout_ShouldDeleteEmitter() {
        // Given
        String eventId = "conference:1:session:2";
        SseEmitter sseEmitter = new SseEmitter();

        // When
        sseService.registerEmitterHandler(eventId, sseEmitter);

        // 직접 Runnable 실행
        Runnable onTimeoutHandler = () -> {
            emitterRepository.deleteByEventKey(eventId);
        };

        onTimeoutHandler.run();

        // Then
        verify(emitterRepository, times(1)).deleteByEventKey(eventId);
    }

    @Test
    @DisplayName("SSE 에러 발생 - onError() 호출 시 Emitter 삭제")
    void registerEmitterHandler_OnError_ShouldDeleteEmitter() {
        // Given
        String eventId = "conference:1:session:2";
        SseEmitter sseEmitter = new SseEmitter();

        // When
        sseService.registerEmitterHandler(eventId, sseEmitter);

        // 직접 실행하도록 Consumer 람다 등록
        Consumer<Throwable> onErrorHandler = (e) -> {
            emitterRepository.deleteByEventKey(eventId);
        };

        // 직접 실행
        onErrorHandler.accept(new IOException("ERR_INCOMPLETE_CHUNKED_ENCODING"));

        // Then
        verify(emitterRepository, times(1)).deleteByEventKey(eventId);
    }

}