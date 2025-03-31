package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.*;
import goorm.back.zo6.attend.infrastructure.AttendRedisService;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendServiceTest {
    @InjectMocks
    private AttendService attendService;
    @Mock
    private AttendRepository attendRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AttendRedisService attendRedisService;
    @Mock
    private AttendDtoConverter attendDtoConverter;

    @Test
    @DisplayName("컨퍼런스/세션 기반 예매자들 조회 와 참석 여부 및 행사 메타데이터를 조회 - 성공")
    void getAttendanceSummary_Success(){
        // given
        Long conferenceId = 1L;
        Long sessionId = 1L;
        AttendanceSummaryQuery mockQuery = createAttendanceSummaryQuery();

        when(attendRepository.findAttendanceSummary(conferenceId, sessionId)).thenReturn(mockQuery);
        when(attendRedisService.attendCount(conferenceId,sessionId)).thenReturn(2L);

        // when
        AttendanceSummaryResponse response = attendService.getAttendanceSummary(conferenceId, sessionId);

        // then
        assertEquals(2L, response.attendedCount());
        assertEquals(100, response.capacity());
        assertEquals("세션제목",response.name());
        assertEquals(10L, response.userAttendances().get(0).userId());
        assertEquals("홍길동", response.userAttendances().get(0).userName());
        assertEquals(true, response.userAttendances().get(0).isAttended());
        assertEquals(11L, response.userAttendances().get(1).userId());
        assertEquals("김철수", response.userAttendances().get(1).userName());
        assertEquals(false, response.userAttendances().get(1).isAttended());
    }

    @Test
    @DisplayName("컨퍼런스/세션 기반 예매자들 조회 와 참석 여부 및 행사 메타데이터를 조회 - 예매내역이 없어서 실패")
    void getAttendanceSummary_NoUserFails() {
        // given
        Long conferenceId = 1L;
        Long sessionId = 2L;

        // 결과가 empty
        when(attendRepository.findAttendanceSummary(conferenceId, sessionId))
                .thenThrow(new CustomException(ErrorCode.NO_ATTENDANCE_DATA));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            attendService.getAttendanceSummary(conferenceId, sessionId);
        });

        assertEquals(ErrorCode.NO_ATTENDANCE_DATA, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저 행사 참석 데이터 저장 - 컨퍼런스 참석 성공")
    void registerAttend_ConferenceAttendSuccess() {
        // given
        Long userId = 1L;
        String phone = "010-1111-2222";
        Long conferenceId = 1L;
        Long reservationId = 100L;

        User mockUser = User.builder()
                .email("test@email.com")
                .name("홍길순")
                .phone(phone)
                .build();
        ReflectionTestUtils.setField(mockUser, "id", userId);

        // sessionId가 null → 컨퍼런스 참석
        AttendData attendData = new AttendData(reservationId, null, conferenceId, null);
        Attend mockAttend = Attend.of(userId, reservationId, null, conferenceId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(attendRepository.findAttendInfo(phone, conferenceId, null)).thenReturn(attendData);
        when(attendRepository.save(any(Attend.class))).thenReturn(mockAttend);

        // when
        attendService.registerAttend(userId, conferenceId, null);

        // then
        verify(userRepository, times(1)).findById(userId);
        verify(attendRepository, times(1)).findAttendInfo(phone, conferenceId, null);
        verify(attendRepository, times(1)).save(any(Attend.class));
    }

    @Test
    @DisplayName("유저 행사 참석 데이터 저장 - 세션 참석 성공")
    void registerAttend_SessionAttendSuccess() {
        // given
        Long userId = 1L;
        String phone = "010-1111-2222";
        Long conferenceId = 1L;
        Long sessionId = 2L;
        Long reservationId = 1L;
        Long reservationSessionId = 2L;

        User mockUser = User.builder()
                .email("test@email.com")
                .name("홍길순")
                .phone(phone)
                .build();
        ReflectionTestUtils.setField(mockUser, "id", userId);

        // 세션 참석용 AttendData
        AttendData attendData = new AttendData(reservationId, reservationSessionId, conferenceId, sessionId);
        Attend mockAttend = Attend.of(userId, reservationId, reservationSessionId, conferenceId, sessionId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(attendRepository.findAttendInfo(phone, conferenceId, sessionId)).thenReturn(attendData);
        when(attendRepository.save(any(Attend.class))).thenReturn(mockAttend);

        // when
        attendService.registerAttend(userId, conferenceId, sessionId);

        // then
        verify(userRepository, times(1)).findById(userId);
        verify(attendRepository, times(1)).findAttendInfo(phone, conferenceId, sessionId);
        verify(attendRepository, times(1)).save(any(Attend.class));
    }

    @Test
    @DisplayName("유저 참석 정보를 조회합니다 - 컨퍼런스 참석, 세션 참석 성공")
    void findAllByToken_AttendSuccess() {
        // given
        Long userId = 1L;
        Long conferenceId = 1L;
        boolean isConferenceAttend = true;
        boolean isSessionAttend = true;

        ConferenceInfoResponse mockResponse = new ConferenceInfoResponse(
                conferenceId,
                "컨퍼런스 이름",
                "설명",
                "서울",
                LocalDateTime.of(2024, 3, 1, 9, 0),
                LocalDateTime.of(2024, 3, 1, 18, 0),
                100,
                true,
                "imageKey",
                true,
                isConferenceAttend,
                new ArrayList<>()
        );

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId))
                .thenReturn(mockResponse);
        when(attendDtoConverter.convertConferenceInfoResponse(any(ConferenceInfoResponse.class)))
                .thenAnswer(invocation -> {
                    ConferenceInfoResponse arg = invocation.getArgument(0);
                    return new ConferenceInfoResponse(
                            arg.getId(),
                            arg.getName(),
                            arg.getDescription(),
                            arg.getLocation(),
                            arg.getStartTime(),
                            arg.getEndTime(),
                            arg.getCapacity(),
                            arg.getHasSessions(),
                            "https://mockimage.com/" + arg.getImageUrl(),
                            arg.getIsActive(),
                            arg.isAttend(),
                            arg.getSessions()
                    );
                });

        // when
        ConferenceInfoResponse result = attendService.findAllByToken(userId, conferenceId);


        // then
        assertNotNull(result);
        assertEquals(conferenceId, result.getId());
        assertEquals("컨퍼런스 이름", result.getName());
        assertEquals(isConferenceAttend, result.isAttend());

        verify(attendRepository).findAttendInfoByUserAndConference(userId, conferenceId);
    }

    @Test
    @DisplayName("유저 참석 정보를 조회합니다 - 컨퍼런스 미참석, 세션 미참석 성공")
    void findAllByToken_NoneAttendSuccess() {
        // given
        Long userId = 1L;
        Long conferenceId = 1L;
        boolean isConferenceAttend = false;
        boolean isSessionAttend = false;

        SessionInfo sessionInfo = new SessionInfo(
                2L, "Session 1", 50, "Room A",
                LocalDateTime.of(2024, 3, 1, 10, 0),
                LocalDateTime.of(2024, 3, 1, 12, 0),
                "Summary 1", "발표자", "발표자 소속",
                "s3imageKey", true, isSessionAttend
        );

        ConferenceInfoResponse mockResponse = new ConferenceInfoResponse(
                conferenceId,
                "컨퍼런스 이름",
                "설명",
                "서울",
                LocalDateTime.of(2024, 3, 1, 9, 0),
                LocalDateTime.of(2024, 3, 1, 18, 0),
                100,
                true,
                "imageKey",
                true,
                isConferenceAttend,
                List.of(sessionInfo)
        );

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId))
                .thenReturn(mockResponse);
        when(attendDtoConverter.convertConferenceInfoResponse(any(ConferenceInfoResponse.class)))
                .thenAnswer(invocation -> {
                    ConferenceInfoResponse arg = invocation.getArgument(0);
                    return new ConferenceInfoResponse(
                            arg.getId(),
                            arg.getName(),
                            arg.getDescription(),
                            arg.getLocation(),
                            arg.getStartTime(),
                            arg.getEndTime(),
                            arg.getCapacity(),
                            arg.getHasSessions(),
                            "https://mockimage.com/" + arg.getImageUrl(),
                            arg.getIsActive(),
                            arg.isAttend(),
                            arg.getSessions()
                    );
                });

        // when
        ConferenceInfoResponse result = attendService.findAllByToken(userId, conferenceId);

        // then
        assertNotNull(result);
        assertEquals(conferenceId, result.getId());
        assertEquals(isConferenceAttend, result.isAttend());

        assertEquals(1, result.getSessions().size());
        SessionInfo session = result.getSessions().get(0);
        assertEquals("Session 1", session.getName());
        assertEquals(50, session.getCapacity());
        assertEquals(isSessionAttend, session.isAttend());

        verify(attendRepository).findAttendInfoByUserAndConference(userId, conferenceId);
    }

    @Test
    @DisplayName("유저 참석 정보를 조회합니다 - 컨퍼런스 정보가 없음 실패")
    void findAllByToken_ConferenceNotFound_Fails() {
        // given
        Long userId = 1L;
        Long conferenceId = 1L;

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId))
                .thenThrow(new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // when
        CustomException exception = assertThrows(CustomException.class, () ->
                attendService.findAllByToken(userId, conferenceId)
        );

        // then
        assertEquals(ErrorCode.RESERVATION_NOT_FOUND, exception.getErrorCode());
        verify(attendRepository).findAttendInfoByUserAndConference(userId, conferenceId);
    }

    private AttendanceSummaryQuery createAttendanceSummaryQuery() {
        List<UserAttendanceResponse> users = List.of(
                new UserAttendanceResponse(10L, "홍길동", true),
                new UserAttendanceResponse(11L, "김철수", false)
        );

        return new AttendanceSummaryQuery("세션제목", 100, users);
    }
}