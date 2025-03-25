package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.AttendanceSummaryResponse;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.attend.dto.SessionInfoDto;
import goorm.back.zo6.attend.dto.UserAttendanceResponse;
import goorm.back.zo6.attend.infrastructure.AttendRedisService;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AttendService {
    private final AttendRepository attendRepository;
    private final UserRepository userRepository;
    private final AttendRedisService attendRedisService;

    public AttendanceSummaryResponse getAttendanceSummary(Long conferenceId, Long sessionId){
        List<Tuple> results = attendRepository.findUsersWithAttendanceAndMeta(conferenceId, sessionId);
        String title = results.get(0).get(3, String.class);
        Integer capacity = results.get(0).get(4, Integer.class);
        long attendCount = attendRedisService.attendCount(conferenceId,sessionId);
        List<UserAttendanceResponse> userAttendances = new ArrayList<>();
        for(Tuple tuple: results){
            Long userId = tuple.get(0, Long.class);
            String name = tuple.get(1, String.class);
            boolean isAttended = tuple.get(2, Boolean.class);
            userAttendances.add(UserAttendanceResponse.of(userId,name,isAttended));
        }

        return AttendanceSummaryResponse.of(title, capacity,attendCount, userAttendances);
    }

    @Transactional
    public void registerAttend(Long userId, Long conferenceId, Long sessionId) {
        log.info("참석 정보 rdb 저장, userId : {}, conferenceId : {} ,sessionId : {}",userId,conferenceId,sessionId);
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Reservation & 관련 데이터 조회 (phone 기반)
        Tuple attendData = findAttendData(user.getPhone(), conferenceId, sessionId);
        // attendData 기반으로 Attend 생성
        Attend attend = convertToAttend(user.getId(), attendData);

        attendRepository.save(attend);
    }

    public ConferenceInfoDto findAllByToken(Long userId, Long conferenceId) {
        List<Tuple> results = attendRepository.findAttendInfoByUserAndConference(userId, conferenceId);

        if (results.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        Tuple conferenceTuple  = results.get(0);

        return convertToConferenceInfoDto(conferenceTuple, results);
    }

    private Tuple findAttendData(String phone, Long conferenceId, Long sessionId) {
        List<Tuple> results = attendRepository.findAttendData(phone, conferenceId, sessionId);
        if (results.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return results.get(0);
    }

    private Attend convertToAttend(Long userId, Tuple tuple) {
        Long reservationId = tuple.get(0, Long.class);
        Long reservationSessionId = tuple.get(1, Long.class);
        Long validConferenceId = tuple.get(2, Long.class);
        Long validSessionId = tuple.get(3, Long.class); // null 허용

        return Attend.of(userId, reservationId, reservationSessionId, validConferenceId, validSessionId);
    }

    private ConferenceInfoDto convertToConferenceInfoDto(Tuple conferenceTuple, List<Tuple> allTuples) {
        return new ConferenceInfoDto(
                conferenceTuple.get(0, Long.class), // c.id
                conferenceTuple.get(1, String.class), // c.name
                conferenceTuple.get(2, String.class), // c.description
                conferenceTuple.get(3, String.class), // c.location
                conferenceTuple.get(4, LocalDateTime.class), // c.startTime
                conferenceTuple.get(5, LocalDateTime.class), // c.endTime
                conferenceTuple.get(6, Integer.class), // c.capacity
                conferenceTuple.get(7, Boolean.class), // c.hasSession
                conferenceTuple.get(8, String.class), // c.imageKey
                conferenceTuple.get(9, Boolean.class), // c.isActive
                conferenceTuple.get(10, Boolean.class), // isAttend
                extractSessionInfoList(allTuples)
        );
    }

    private List<SessionInfoDto> extractSessionInfoList(List<Tuple> sessionTuples) {
        return sessionTuples.stream()
                .filter(sessionTuple -> sessionTuple.get(11, Long.class) != null) // 세션 존재하는 경우만
                .map(sessionTuple -> new SessionInfoDto(
                        sessionTuple.get(11, Long.class),   // s.id
                        sessionTuple.get(12, String.class), // s.name
                        sessionTuple.get(13, Integer.class),// s.capacity
                        sessionTuple.get(14, String.class), // s.location
                        sessionTuple.get(15, LocalDateTime.class), // s.startTime
                        sessionTuple.get(16, LocalDateTime.class), // s.endTime
                        sessionTuple.get(17, String.class), // s.summary
                        sessionTuple.get(18, String.class), // s.speakerName
                        sessionTuple.get(19, String.class), // s.speakerOrganization
                        sessionTuple.get(20, String.class), // s.speakerImageKey
                        sessionTuple.get(21, Boolean.class),// s.isActive
                        sessionTuple.get(22, Boolean.class) // s.isAttend
                ))
                .collect(Collectors.toList());

    }

}
