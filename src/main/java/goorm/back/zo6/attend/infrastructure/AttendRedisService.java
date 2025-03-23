package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.dto.AttendInfo;
import goorm.back.zo6.attend.dto.AttendKeys;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Log4j2
public class AttendRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 유저 참석 확인
    public AttendInfo saveUserAttendance(Long conferenceId, Long sessionId, Long userId){
        if(conferenceId == null){
            throw new CustomException(ErrorCode.MISSING_REQUIRED_PARAMETER);
        }

        AttendKeys keys = generateKeys(conferenceId, sessionId);

        AttendInfo attendInfo = processAttendance(keys, userId);

        return attendInfo;
    }

    // 참석 처리
    private AttendInfo processAttendance(AttendKeys keys, Long userId){
        log.info("{} 참가", keys.isSession() ? "Session" : "Conference");

        boolean isNewUser = addNewUserToAttendance(keys.attendanceKey(), userId);

        if (isNewUser) {
            incrementCountIfNew(keys.countKey());
        }

        // count 조회
        long count = getCurrentCount(keys.countKey());

        return AttendInfo.of(isNewUser, count);
    }

    // 모든 참석 키 삭제 - swagger 테스트 시 사용
    public void deleteAllKeys(){
        redisTemplate.delete(redisTemplate.keys("*"));
    }

    private boolean addNewUserToAttendance(String attendanceKey, Long userId) {
        Long added = redisTemplate.opsForSet().add(attendanceKey, userId.toString());
        if(added > 0){
            expireAtNextDay5AM(attendanceKey);
            return true;
        }
        return false;
    }

    private void incrementCountIfNew(String countKey) {
        boolean isNewKey = Boolean.FALSE.equals(redisTemplate.hasKey(countKey));
        redisTemplate.opsForValue().increment(countKey);
        if (isNewKey) {
            expireAtNextDay5AM(countKey);
        }
    }

    private long getCurrentCount(String countKey) {
        String countStr = redisTemplate.opsForValue().get(countKey);
        return (countStr != null) ? Long.parseLong(countStr) : 0L;
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

    public void expireAtNextDay5AM(String redisKey) {
        // 현재 시각
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

        // 다음날 5시 (오전)
        ZonedDateTime next5am = now.plusDays(1).withHour(5).withMinute(0).withSecond(0).withNano(0);

        // Date로 변환 후 expireAt
        Date expireDate = Date.from(next5am.toInstant());
        redisTemplate.expireAt(redisKey, expireDate);
    }

}
