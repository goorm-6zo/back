package goorm.back.zo6.sse.presentation;

import goorm.back.zo6.sse.infrastructure.EmitterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class SseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmitterRepository emitterRepository;

    @Test
    @DisplayName("SSE 구독 요청 - SseEmitter 반환 구독 성공")
    void subscribe_Success() throws Exception {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;

        // when & then
        mockMvc.perform(get("/api/v1/sse/subscribe")
                        .param("conferenceId", String.valueOf(conferenceId))
                        .param("sessionId", String.valueOf(sessionId))
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_EVENT_STREAM_VALUE));

        String eventKey = "conference:" + conferenceId + ":session:" + sessionId;
        SseEmitter savedEmitter = emitterRepository.findEmitterByKey(eventKey);

        assertNotNull(savedEmitter);
    }

    @Test
    @DisplayName("SSE 구독 요청 - conferenceId 없이 요청하면 400 Bad Request")
    void subscribe_NoneConferenceFails() throws Exception {
        // given
        Long sessionId = 2L;
        // when & then
        mockMvc.perform(get("/api/v1/sse/subscribe")
                        .param("sessionId", String.valueOf(sessionId))
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SSE 구독 요청 - Emitter 가 저장소에 저장되지 않으면 예외 발생")
    void subscribe_EmitterNotStored_ShouldReturnNull() throws Exception {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;

        // 정상적으로 요청
        mockMvc.perform(get("/api/v1/sse/subscribe")
                        .param("conferenceId", String.valueOf(conferenceId))
                        .param("sessionId", String.valueOf(sessionId))
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(status().isOk());

        // 저장된 SSE Emitter 확인 (없는 경우 예외 발생)
        String invalidEventKey = "conference:-1:session:-2"; // 존재하지 않는 key
        SseEmitter savedEmitter = emitterRepository.findEmitterByKey(invalidEventKey);

        assertNull(savedEmitter); // 저장되지 않았어야 함
    }
}