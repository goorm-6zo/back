package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

    @Test
    @DisplayName("유저 행사 참석 데이터 저장 - 컨퍼런스 참석 성공")
    void registerAttend_ConferenceAttendSuccess() {
        // given
        Long userId = 1L;
        String phone = "010-1111-2222";
        Long conferenceId = 1L;
        Long reservationId = 1L;

        User mockUser = User.builder().email("test@email").name("홍길순").phone(phone).build();
        ReflectionTestUtils.setField(mockUser,"id",1L);

        Tuple mockTuple = createRegisterTuple(reservationId, null, conferenceId, null);
        List<Tuple> mockResults = Collections.singletonList(mockTuple);
        Attend mockAttend = Attend.of(userId, reservationId, null, conferenceId, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(attendRepository.findAttendData(phone,conferenceId, null)).thenReturn(mockResults);
        when(attendRepository.save(any(Attend.class))).thenReturn(mockAttend);

        // when
        attendService.registerAttend(userId,conferenceId,null);

        // then
        verify(userRepository, times(1)).findById(userId);
        verify(attendRepository,times(1)).findAttendData(phone,conferenceId,null);
        verify(attendRepository,times(1)).save(any(Attend.class));
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

        User mockUser = User.builder().email("test@email").name("홍길순").phone(phone).build();
        ReflectionTestUtils.setField(mockUser,"id",1L);

        Tuple mockTuple = createRegisterTuple(reservationId, reservationSessionId, conferenceId, sessionId);
        List<Tuple> mockResults = Collections.singletonList(mockTuple);
        Attend mockAttend = Attend.of(userId, reservationId, reservationSessionId, conferenceId, sessionId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(attendRepository.findAttendData(phone,conferenceId, sessionId)).thenReturn(mockResults);
        when(attendRepository.save(any(Attend.class))).thenReturn(mockAttend);

        // when
        attendService.registerAttend(userId,conferenceId,sessionId);

        // then
        verify(userRepository, times(1)).findById(userId);
        verify(attendRepository,times(1)).findAttendData(phone,conferenceId,sessionId);
        verify(attendRepository,times(1)).save(any(Attend.class));
    }

    @Test
    @DisplayName("유저 참석 정보를 조회합니다 - 컨퍼런스 참석, 세션 참석 성공")
    void findAllByToken_AttendSuccess() {
        // given
        Long userId = 1L;
        Long conferenceId = 1L;
        boolean isConferenceAttend = true;
        boolean isSessionAttend = true;

        List<Tuple> mockResults = new ArrayList<>();
        Tuple mockTuple = createGetTuple(conferenceId, isConferenceAttend, 2L, "Session 1", 50, "Room A",
                LocalDateTime.now(), LocalDateTime.now(),"Summary 1",  "발표자", "발표자 소속", "s3imageKey",true, isSessionAttend);
        mockResults.add(mockTuple);

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId)).thenReturn(mockResults);

        // when
        ConferenceInfoDto result = attendService.findAllByToken(userId, conferenceId);

        // then
        assertNotNull(result);
        assertEquals(conferenceId, result.getId());
        assertEquals(1, result.getSessions().size());
        assertEquals(result.isAttend(), isConferenceAttend);
        assertEquals(result.getSessions().get(0).isAttend(), isSessionAttend);
        assertEquals("Session 1", result.getSessions().get(0).getName());
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

        List<Tuple> mockResults = new ArrayList<>();
        Tuple mockTuple = createGetTuple(conferenceId, isConferenceAttend, 2L, "Session 1", 50, "Room A",
                LocalDateTime.now(), LocalDateTime.now(),"Summary 1", "발표자", "발표자 소속","s3imageKey",true,isSessionAttend);
        mockResults.add(mockTuple);

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId)).thenReturn(mockResults);

        // when
        ConferenceInfoDto result = attendService.findAllByToken(userId, conferenceId);

        // then
        assertNotNull(result);
        assertEquals(conferenceId, result.getId());
        assertEquals(1, result.getSessions().size());
        assertEquals(result.isAttend(), isConferenceAttend);
        assertEquals(result.getSessions().get(0).isAttend(), isSessionAttend);
        assertEquals("Session 1", result.getSessions().get(0).getName());
        verify(attendRepository).findAttendInfoByUserAndConference(userId, conferenceId);
    }

    @Test
    @DisplayName("유저 참석 정보를 조회합니다 - 컨퍼런스 정보가 없음 실패")
    void findAllByToken_ConferenceNotFound_Fails() {
        // given
        Long userId = 1L;
        Long conferenceId = 1L;

        when(attendRepository.findAttendInfoByUserAndConference(userId, conferenceId)).thenReturn(Collections.emptyList());

        // when
        ConferenceInfoDto result = attendService.findAllByToken(userId, conferenceId);

        // then
        assertNull(result);
        verify(attendRepository).findAttendInfoByUserAndConference(userId, conferenceId);
    }


    // Register(참석정보(컨퍼런스 or 세션) Tuple 생성 메서드
    private Tuple createRegisterTuple(Long reservationId, Long reservationSessionId, Long conferenceId, Long sessionId) {
        Tuple mockTuple = mock(Tuple.class);
        when(mockTuple.get(0, Long.class)).thenReturn(reservationId);
        when(mockTuple.get(1, Long.class)).thenReturn(reservationSessionId);
        when(mockTuple.get(2, Long.class)).thenReturn(conferenceId);
        when(mockTuple.get(3, Long.class)).thenReturn(sessionId);
        return mockTuple;
    }

    // Get(참석상태 & 예매 컨퍼런스 정보 조회) Tuple 생성 메서드
    private Tuple createGetTuple(Long conferenceId, Boolean isAttended, Long sessionId, String sessionName,
                                  Integer capacity, String location, LocalDateTime startTime, LocalDateTime endTime, String summary, String speakerName, String speakerOrganization, String speakerImageKey, Boolean isActive,Boolean attended) {
        Tuple mockTuple = mock(Tuple.class);
        when(mockTuple.get(0, Long.class)).thenReturn(conferenceId);
        when(mockTuple.get(1, String.class)).thenReturn("Conference Name");
        when(mockTuple.get(2, String.class)).thenReturn("Description");
        when(mockTuple.get(3, String.class)).thenReturn("Location");
        when(mockTuple.get(4, String.class)).thenReturn("Area");
        when(mockTuple.get(5, LocalDateTime.class)).thenReturn(LocalDateTime.of(2025, 3, 18, 9, 0));
        when(mockTuple.get(6, LocalDateTime.class)).thenReturn(LocalDateTime.of(2025, 3, 18, 17, 0));
        when(mockTuple.get(7, Integer.class)).thenReturn(100);
        when(mockTuple.get(8, Boolean.class)).thenReturn(true);
        when(mockTuple.get(9, String.class)).thenReturn("test.png");
        when(mockTuple.get(10, Boolean.class)).thenReturn(true);
        when(mockTuple.get(11, Boolean.class)).thenReturn(isAttended);

        if (sessionId != null) {
            when(mockTuple.get(12, Long.class)).thenReturn(sessionId);
            when(mockTuple.get(13, String.class)).thenReturn(sessionName);
            when(mockTuple.get(14, Integer.class)).thenReturn(capacity);
            when(mockTuple.get(15, String.class)).thenReturn(location);
            when(mockTuple.get(16, LocalDateTime.class)).thenReturn(startTime);
            when(mockTuple.get(17, LocalDateTime.class)).thenReturn(endTime);
            when(mockTuple.get(18, String.class)).thenReturn(summary);
            when(mockTuple.get(19, String.class)).thenReturn(speakerName);
            when(mockTuple.get(20, String.class)).thenReturn(speakerOrganization);
            when(mockTuple.get(21, String.class)).thenReturn(speakerImageKey);
            when(mockTuple.get(22, Boolean.class)).thenReturn(isActive);
            when(mockTuple.get(23, Boolean.class)).thenReturn(attended);
        } else {
            when(mockTuple.get(8, Long.class)).thenReturn(null);
            when(mockTuple.get(9, String.class)).thenReturn(null);
            when(mockTuple.get(10, Integer.class)).thenReturn(null);
            when(mockTuple.get(11, String.class)).thenReturn(null);
            when(mockTuple.get(12, LocalDateTime.class)).thenReturn(null);
            when(mockTuple.get(13, LocalDateTime.class)).thenReturn(null);
            when(mockTuple.get(14, String.class)).thenReturn(null);
            when(mockTuple.get(15, String.class)).thenReturn(speakerName);
            when(mockTuple.get(16, String.class)).thenReturn(speakerOrganization);
            when(mockTuple.get(17, Boolean.class)).thenReturn(isActive);
            when(mockTuple.get(18, Boolean.class)).thenReturn(null);
        }

        return mockTuple;
    }
}