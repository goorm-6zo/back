package goorm.back.zo6.sse.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.sse.dto.AttendanceKeys;
import goorm.back.zo6.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Log4j2
public class AttendanceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SseService sseService;

    // 유저 참석 확인
    public void saveUserAttendance(Long conferenceId, Long sessionId, Long userId, long timestamp){
        if(conferenceId == null){
            throw new CustomException(ErrorCode.MISSING_REQUIRED_PARAMETER);
        }

        AttendanceKeys keys = generateKeys(conferenceId, sessionId);

        long count = processAttendance(keys, userId, timestamp);

        sseService.sendAttendanceCount(conferenceId, sessionId, count);
    }

    // 참석 처리
    private long processAttendance(AttendanceKeys keys, Long userId, long timestamp){
        log.info("{} 참가", keys.isSession() ? "Session" : "Conference");

        Long added = redisTemplate.opsForSet().add(keys.attendanceKey(), userId.toString());
        if(added != null && added > 0){
            redisTemplate.opsForValue().increment(keys.countKey());
        }

        redisTemplate.expire(keys.attendanceKey(), Duration.ofSeconds(timestamp));

        String countStr = redisTemplate.opsForValue().get(keys.countKey());
        return (countStr != null) ? Integer.parseInt(countStr) : 0;
    }

    // Conference 및 Session 의 Redis 키를 생성
    private AttendanceKeys generateKeys(Long conferenceId, Long sessionId){
        if(sessionId == null){
            return AttendanceKeys.builder()
                    .attendanceKey("conference:" + conferenceId)
                    .countKey("conference_count:" + conferenceId)
                    .isSession(false)
                    .build();
        }

        return AttendanceKeys.builder()
                .attendanceKey("conference:" + conferenceId + ":session:" + sessionId)
                .countKey("conference:" + conferenceId + ":session_count:" + sessionId)
                .isSession(true)
                .build();
    }
}
