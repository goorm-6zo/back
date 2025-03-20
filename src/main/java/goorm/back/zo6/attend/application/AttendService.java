package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.attend.dto.SessionInfoDto;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AttendService {
    private final AttendRepository attendRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerAttend(Long userId, Long conferenceId, Long sessionId) {
        log.info("참석 정보 rdb 저장, userId : {}, conferenceId : {} ,sessionId : {}",userId,conferenceId,sessionId);
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Reservation & 관련 데이터 조회 (phone 기반)
        List<Tuple> results = attendRepository.findAttendData(user.getPhone(), conferenceId, sessionId);

        Tuple result = results.get(0);

        // 필요한 데이터만 가져오기
        Long reservationId = result.get(0, Long.class);
        Long reservationSessionId = result.get(1, Long.class);
        Long validConferenceId = result.get(2, Long.class);
        Long validSessionId = result.get(3, Long.class); // session이 없을 경우 null 허용

        // Attend 객체 생성 및 저장
        Attend attend = Attend.of(user.getId(), reservationId, reservationSessionId, validConferenceId, validSessionId);
        attendRepository.save(attend);
        log.info("참석 정보 rdb 저장 완료, userId : {}, conferenceId : {} ,sessionId : {}",attend.getUserId(),attend.getConferenceId(),attend.getSessionId());
    }

    public ConferenceInfoDto findAllByToken(Long userId, Long conferenceId) {
        List<Tuple> results = attendRepository.findAttendInfoByUserAndConference(userId, conferenceId);

        if (results.isEmpty()) {
            return null; // 데이터가 없으면 null 반환 (또는 Optional 사용)
        }

        Tuple firstTuple = results.get(0); // 첫 번째 결과를 가져옴

        ConferenceInfoDto conferenceInfo = new ConferenceInfoDto(
                firstTuple.get(0,Long.class), // c.id
                firstTuple.get(1, String.class), // c.name
                firstTuple.get(2, String.class), // c.description
                firstTuple.get(3, String.class), // c.location
                firstTuple.get(4, LocalDateTime.class), // c.conferenceAt
                firstTuple.get(5, Integer.class), // c.capacity
                firstTuple.get(6, Boolean.class), // c.hasSession
                firstTuple.get(7, String.class), // c.imageUrl
                firstTuple.get(8, Boolean.class), // c.isActive
                firstTuple.get(9,Boolean.class), // c.isAttend
                new ArrayList<>() // 세션 리스트 초기화
        );

        // 세션 정보 추가
        results.forEach(tuple -> {
            if (tuple.get(10, Long.class) != null) {
                SessionInfoDto sessionInfo = new SessionInfoDto(
                        tuple.get(10, Long.class), // s.id
                        tuple.get(11, String.class), // s.name
                        tuple.get(12, Integer.class), // s.capacity
                        tuple.get(13, String.class), // s.location
                        tuple.get(14, LocalDateTime.class), // s.time
                        tuple.get(15, String.class), // s.summary
                        tuple.get(16, String.class), // s.speakerName
                        tuple.get(17, String.class), // s.speakerOrganization
                        tuple.get(18,String.class), // s.speakerImageKey
                        tuple.get(19, Boolean.class), // s.isActive
                        tuple.get(20, Boolean.class) // s.isAttend
                );
                conferenceInfo.getSessions().add(sessionInfo);
            }
        });

        return conferenceInfo;
    }
}
