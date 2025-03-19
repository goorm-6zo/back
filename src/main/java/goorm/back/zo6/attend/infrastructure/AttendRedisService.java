package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.dto.AttendInfo;
import goorm.back.zo6.attend.dto.AttendKeys;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.sse.application.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Log4j2
public class AttendRedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SseService sseService;

    // 유저 참석 확인
    public AttendInfo saveUserAttendance(Long conferenceId, Long sessionId, Long userId, long timestamp){
        if(conferenceId == null){
            throw new CustomException(ErrorCode.MISSING_REQUIRED_PARAMETER);
        }

        AttendKeys keys = generateKeys(conferenceId, sessionId);

        AttendInfo attendInfo = processAttendance(keys, userId, timestamp);

        return attendInfo;
    }

    // 참석 처리
    private AttendInfo processAttendance(AttendKeys keys, Long userId, long timestamp){
        log.info("{} 참가", keys.isSession() ? "Session" : "Conference");

        Long added = redisTemplate.opsForSet().add(keys.attendanceKey(), userId.toString());
        log.info("added 값 : {}", added);
        boolean alreadyAttend = true;

        if(added > 0){
            alreadyAttend = false;
            redisTemplate.opsForValue().increment(keys.countKey());
        }

        log.info("alreadyAttend 값 : {}", alreadyAttend);
        redisTemplate.expire(keys.attendanceKey(), Duration.ofSeconds(timestamp));

        String countStr = redisTemplate.opsForValue().get(keys.countKey());
        long count = (countStr != null) ? Long.parseLong(countStr) : 0;
        return AttendInfo.of(alreadyAttend, count);
    }

    // Conference 및 Session 의 Redis 키를 생성
    private AttendKeys generateKeys(Long conferenceId, Long sessionId){
        if(sessionId == null){
            return AttendKeys.builder()
                    .attendanceKey("conference:" + conferenceId)
                    .countKey("conference_count:" + conferenceId)
                    .isSession(false)
                    .build();
        }

        return AttendKeys.builder()
                .attendanceKey("conference:" + conferenceId + ":session:" + sessionId)
                .countKey("conference:" + conferenceId + ":session_count:" + sessionId)
                .isSession(true)
                .build();
    }
}
