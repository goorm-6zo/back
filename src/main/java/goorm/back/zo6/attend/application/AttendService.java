package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.AttendInfoResponse;
import goorm.back.zo6.attend.dto.AttendResponse;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.attend.dto.SessionInfoDto;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationSession;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AttendService {
    private final ReservationRepository reservationRepository;
    private final AttendRepository attendRepository;
    private final UserRepository userRepository;

    @Transactional
    public AttendResponse registerAttend(Long userId, Long conferenceId, Long sessionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findByPhoneAndConferenceId(user.getPhone(), conferenceId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        Set<ReservationSession> reservationSessions = reservation.getReservationSessions();
        Optional<ReservationSession> reservationSession = reservationSessions.stream()
                .filter(session -> session.getSession().getId().equals(sessionId))
                .findFirst();

        Long reservationSessionId = reservationSession.map(ReservationSession::getId).orElse(null);
        Long validSessionId = reservationSessionId == null? null: reservationSession.get().getSession().getId();
        Attend attend = Attend.of(user.getId(), reservation.getId(), reservationSessionId,reservation.getId(), validSessionId);
        attend = attendRepository.save(attend);

        return AttendResponse.from(attend);
    }

    public List<ConferenceInfoDto> findAllByToken(Long userId, Long conferenceId) {
        List<Tuple> results = attendRepository.findAttendInfoByUserAndConference(userId, conferenceId);

        // 컨퍼런스를 Map에 저장하여 중복 제거
        Map<Long, ConferenceInfoDto> conferenceMap = new HashMap<>();

        results.forEach(tuple -> {
            Long conferenceIdFromDb = tuple.get(0, Long.class);
            boolean isConferenceAttended = tuple.get(7, Boolean.class);

            ConferenceInfoDto conferenceInfo = conferenceMap.getOrDefault(conferenceIdFromDb, new ConferenceInfoDto(
                    conferenceIdFromDb,
                    tuple.get(1, String.class),
                    tuple.get(2, String.class),
                    tuple.get(3, String.class),
                    tuple.get(4, LocalDateTime.class),
                    tuple.get(5, Integer.class),
                    tuple.get(6, Boolean.class),
                    isConferenceAttended,
                    new ArrayList<>()
            ));

            // 세션 정보 추가
            if (tuple.get(8, Long.class) != null) {
                SessionInfoDto sessionInfo = new SessionInfoDto(
                        tuple.get(8, Long.class), // s.id
                        tuple.get(9, String.class), // s.name
                        tuple.get(10, Integer.class), // s.capacity
                        tuple.get(11, String.class), // s.location
                        tuple.get(12, LocalDateTime.class), // s.time
                        tuple.get(13, String.class), // s.summary
                        tuple.get(14, Boolean.class) // 세션 참석 여부
                );
                conferenceInfo.getSessions().add(sessionInfo);
            }

            // 중복 방지를 위해 Map에 저장
            conferenceMap.put(conferenceIdFromDb, conferenceInfo);
        });

        return new ArrayList<>(conferenceMap.values());
    }

    public void deleteByToken(Long userId, Long reservationId, Long reservationSessionId) {
        attendRepository.deleteByUserIdAndReservationIdAndReservationSessionId(userId, reservationId, reservationSessionId);
    }
}
