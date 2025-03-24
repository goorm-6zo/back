package goorm.back.zo6.reservation.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConferenceJpaRepository conferenceJpaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    @BeforeEach
    void setUpSecurityContext() {

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("테스트유저");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        User user = User.builder()
                .email("test@gmail.com")
                .name("테스트유저")
                .phone("01011112222")
                .build();
        ReflectionTestUtils.setField(user,"id",1L);

        lenient().when(userRepository.findByName("테스트유저")).thenReturn(Optional.of(user));
    }

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    @DisplayName("유효한 정보로 예약 등록 성공")
    void createReservation_Success() {

        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);

        conference.addSession(session);

        when(conferenceJpaRepository.findById(conference.getId()))
                .thenReturn(Optional.of(conference));

        ReservationRequest request = new ReservationRequest(
                conference.getId(),
                List.of(session.getId()),
                "홍길순",
                "01011112222"
        );

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationServiceImpl.createReservation(request);

        assertThat(response).isNotNull();
        assertThat(response.getConference().getConferenceId()).isEqualTo(conference.getId());
        assertThat(response.getSessions()).hasSize(1).extracting(ReservationResponse.SessionInfo::getSessionId).contains(session.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("존재하지 않는 컨퍼런스일 경우 예약 실패")
    void createReservation_Fail_ConferenceNotFound() {

        Long invalidConferenceId = 999L;
        when(conferenceJpaRepository.findById(invalidConferenceId)).thenReturn(Optional.empty());

        ReservationRequest request = new ReservationRequest(
                invalidConferenceId,
                null,
                "홍길동",
                "01011112222"
        );

        assertThatThrownBy(() -> reservationServiceImpl.createReservation(request)).isInstanceOf(CustomException.class);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("예약 상세 조회 성공")
    void getReservationDetailsById_Success() {

        User mockUser = User.builder()
                .email("test@gmail.com")
                .name("테스트유저")
                .phone("01011112222")
                .build();
        ReflectionTestUtils.setField(mockUser,"id",1L);

        lenient().when(userRepository.findByName("테스트유저")).thenReturn(Optional.of(mockUser));

        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);

        Reservation reservation = Reservation.builder()
                .id(123L)
                .conference(conference)
                .status(ReservationStatus.CONFIRMED)
                .user(mockUser)
                .name(mockUser.getName())
                .phone(mockUser.getPhone())
                .build();

        reservation.addSession(session);

        when(reservationRepository.findById(reservation.getId())).thenReturn(Optional.of(reservation));

        ReservationResponse response = reservationServiceImpl.getReservationDetailsById(reservation.getId());

        assertThat(response).isNotNull();
        assertThat(response.getReservationId()).isEqualTo(reservation.getId());
        assertThat(response.getConference().getConferenceId()).isEqualTo(conference.getId());
        assertThat(response.getSessions()).hasSize(1);
        assertThat(response.getSessions().get(0).getSessionId()).isEqualTo(session.getId());
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    @DisplayName("임시 예약 생성 성공")
    void createTemporaryReservation_Sucess() {

        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        when(conferenceJpaRepository.findById(conference.getId())).thenReturn(Optional.of(conference));

        ReservationRequest request = new ReservationRequest(
                conference.getId(),
                Collections.emptyList(),
                "김철수",
                "01033334444"
        );

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationServiceImpl.createTemporaryReservation(request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.TEMPORARY);
        assertThat(response.getConference().getConferenceId()).isEqualTo(conference.getId());
    }

    @Test
    @DisplayName("전화번호를 기반으로 유저 연동 성공")
    void linkReservationByPhoneAndUser_Success() {

        String inputPhone = "01011112222";

        User user = User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone(inputPhone)
                .role(Role.of("USER"))
                .build();
        ReflectionTestUtils.setField(user,"id",1L);

        Reservation reservation = Reservation.builder()
                .id(1L)
                .conference(ConferenceFixture.컨퍼런스())
                .name("홍길순")
                .phone(inputPhone)
                .status(ReservationStatus.TEMPORARY)
                .build();

        when(userRepository.findByPhone(inputPhone)).thenReturn(Optional.of(user));
        when(reservationRepository.findAllByPhoneAndStatus(anyString(), eq(ReservationStatus.TEMPORARY))).thenReturn(List.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationServiceImpl.linkReservationByPhone(inputPhone);

        assertNotNull(response);
        assertEquals(user.getId(), reservation.getUser().getId());
        assertEquals(inputPhone, reservation.getPhone());

        verify(reservationRepository).findAllByPhoneAndStatus(anyString(), eq(ReservationStatus.TEMPORARY));
        verify(userRepository).findByPhone(inputPhone);
        verify(reservationRepository, atLeastOnce()).save(any(Reservation.class));
    }
}