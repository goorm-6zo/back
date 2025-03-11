package goorm.back.zo6.conference.infrastructure.attendance;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final RedisTemplate<String, String> redisTemplate;

    // 유저 참석 확인
    public boolean isUserAttended(Long conferenceId, Long sessionId, Long userId){
        String key = getRelevantKey(conferenceId, sessionId);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }

    public void saveUserAttendance(Long conferenceId, Long sessionId, Long userId, long timestamp){
        String key = getRelevantKey(conferenceId, sessionId);
        redisTemplate.opsForSet().add(key, userId.toString());
        redisTemplate.expire(key, Duration.ofSeconds(timestamp));
    }

    public void deleteAttendanceData(Long conferenceId, Long sessionId){
        String key = getRelevantKey(conferenceId,sessionId);
        redisTemplate.delete(key);
    }

    private String getRelevantKey(Long conferenceId, Long sessionId){
        if(conferenceId != null) return "conference:" + conferenceId;

        if(sessionId != null) return "conferenceSession:" + sessionId;

        throw new CustomException(ErrorCode.MISSING_REQUIRED_PARAMETER);
    }
}
