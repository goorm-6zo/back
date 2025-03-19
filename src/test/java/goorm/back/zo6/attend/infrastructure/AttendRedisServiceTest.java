package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.dto.AttendKeys;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.sse.service.SseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.NoInteractions;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendRedisServiceTest {

    @InjectMocks
    private AttendRedisService attendRedisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private SseService sseService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    @DisplayName("유저 참석 데이터를 저장 후 count 리턴 - 컨퍼런스 참석 성공")
    void saveUserAttendance_ConferenceSuccess() {
        // given
        Long conferenceId = 1L;
        Long userId = 1L;
        long timestamp = 300L;
        AttendKeys keys = AttendKeys.builder()
                .attendanceKey("conference:" + conferenceId)
                .countKey("conference_count:" + conferenceId)
                .isSession(false)
                .build();

        when(setOperations.add(keys.attendanceKey(), userId.toString())).thenReturn(1L);
        when(valueOperations.get(keys.countKey())).thenReturn("1");
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        boolean alreadyAttended = attendRedisService.saveUserAttendance(conferenceId, null, userId, timestamp);

        // then
        assertThat(alreadyAttended).isFalse();
        verify(setOperations, times(1)).add(keys.attendanceKey(), userId.toString());
        verify(valueOperations, times(1)).increment(keys.countKey());
        verify(redisTemplate, times(1)).expire(keys.attendanceKey(), Duration.ofSeconds(timestamp));
        verify(sseService, times(1)).sendAttendanceCount(conferenceId, null,1);
    }

    @Test
    @DisplayName("유저 참석 데이터를 저장 후 count 리턴 - 세션 참석 성공")
    void saveUserAttendance_SessionSuccess() {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;
        Long userId = 1L;
        long timestamp = 300L;
        AttendKeys keys = AttendKeys.builder()
                .attendanceKey("conference:" + conferenceId + ":session:" + sessionId)
                .countKey("conference:" + conferenceId + ":session_count:" + sessionId)
                .isSession(true)
                .build();

        when(setOperations.add(keys.attendanceKey(), userId.toString())).thenReturn(1L);
        when(valueOperations.get(keys.countKey())).thenReturn("1");
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        boolean alreadyAttended = attendRedisService.saveUserAttendance(conferenceId, sessionId, userId, timestamp);

        // then
        assertThat(alreadyAttended).isFalse();
        verify(setOperations, times(1)).add(keys.attendanceKey(), userId.toString());
        verify(valueOperations, times(1)).increment(keys.countKey());
        verify(redisTemplate, times(1)).expire(keys.attendanceKey(), Duration.ofSeconds(timestamp));
        verify(sseService, times(1)).sendAttendanceCount(conferenceId, sessionId,1);
    }

    @Test
    @DisplayName("유저 참석 데이터를 저장 후 count 리턴 - 컨퍼런스 id null 로 에러 실패")
    void saveUserAttendance_NoneConferenceFails() {
        // given
        Long sessionId = 2L;
        Long userId = 1L;
        Long timestamp = 200L;

        // when
        CustomException customException = assertThrows(CustomException.class, () ->
                attendRedisService.saveUserAttendance(null, sessionId, userId, timestamp));

        // then
        assertEquals(ErrorCode.MISSING_REQUIRED_PARAMETER, customException.getErrorCode());
    }
}