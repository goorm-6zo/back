package goorm.back.zo6.sse.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
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

        long count = 0;
        if(sessionId == null){
            log.info("conference 참가");
            String conferenceKey = "conference:" + conferenceId;
            String conferenceCountKey = "conference_count:" + conferenceId;

            Long addedToConference = redisTemplate.opsForSet().add(conferenceKey, userId.toString());
            if(addedToConference != null && addedToConference > 0){
                redisTemplate.opsForValue().increment(conferenceCountKey);
            }
            redisTemplate.expire(conferenceKey, Duration.ofSeconds(timestamp));

            String countStr = redisTemplate.opsForValue().get(conferenceCountKey);
            count = (countStr != null) ? Integer.parseInt(countStr) : 0;
        }else{
            // 세션 참석 처리
            log.info("session 참가");
            String sessionKey = "conference:" + conferenceId + ":session:" + sessionId;
            String sessionCountKey = "conference:" + conferenceId + ":session_count:" + sessionId;

            Long addedToSession = redisTemplate.opsForSet().add(sessionKey, userId.toString());
            if(addedToSession != null && addedToSession > 0){
                redisTemplate.opsForValue().increment(sessionCountKey);
            }
            redisTemplate.expire(sessionKey, Duration.ofSeconds(timestamp));

            String countStr = redisTemplate.opsForValue().get(sessionCountKey);
            count = (countStr != null) ? Integer.parseInt(countStr) : 0;
        }
        sseService.sendAttendanceCount(conferenceId, sessionId, count);
    }
}
