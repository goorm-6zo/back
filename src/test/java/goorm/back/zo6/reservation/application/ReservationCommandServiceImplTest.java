package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.ReservationFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.fixture.UserFixture;
import goorm.back.zo6.reservation.application.command.ReservationCommandServiceImpl;
import goorm.back.zo6.reservation.application.shared.ReservationFactory;
import goorm.back.zo6.reservation.application.shared.ReservationMapper;
import goorm.back.zo6.reservation.application.shared.ReservationValidator;
import goorm.back.zo6.reservation.application.shared.UserContext;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationCommandServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConferenceValidator conferenceValidator;

    @Mock
    private ReservationValidator reservationValidator;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationFactory reservationFactory;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ReservationCommandServiceImpl reservationCommandService;

    private Conference conference;

    private Session session;

    private User user;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        conference = ConferenceFixture.컨퍼런스_아이디포함();
        session = SessionFixture.세션_아이디포함(conference);
        user = UserFixture.유저();
        reservation = ReservationFixture.확정된예약(conference, List.of(session),  user.getName(), user.getPhone(), user);
    }

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        ReservationRequest request = ReservationRequest.builder()
                .conferenceId(conference.getId())
                .sessionIds(List.of(session.getId()))
                .name(user.getName())
                .phone(user.getPhone())
                .build();

        given(conferenceValidator.findConferenceOrThrow(conference.getId())).willReturn(conference);
        given(reservationValidator.validateSessionReservations(conference, request.sessionIds(), request.name(), request.phone())).willReturn(Set.of(session));
        given(reservationFactory.createReservationEntity(conference, request, Set.of(session), ReservationStatus.CONFIRMED)).willReturn(reservation);
        given(reservationRepository.save(reservation)).willReturn(reservation);

        ReservationResponse expectedResponse = ReservationResponse.builder().build();
        given(reservationMapper.mapToReservationResponse(reservation)).willReturn(expectedResponse);

        ReservationResponse actualResponse = reservationCommandService.createReservation(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("임시 예약 생성 성공")
    void createTemporaryReservation_success() {
        ReservationRequest request = ReservationRequest.builder()
                .conferenceId(conference.getId())
                .sessionIds(List.of(session.getId()))
                .name(user.getName())
                .phone(user.getPhone())
                .build();

        given(conferenceValidator.findConferenceWithSessionsOrThrow(request.conferenceId())).willReturn(conference);
        given(reservationValidator.validateSessionReservations(conference, request.sessionIds(), request.name(), request.phone())).willReturn(Set.of(session));
        given(reservationFactory.createReservationEntity(conference, request, Set.of(session), ReservationStatus.TEMPORARY)).willReturn(reservation);
        given(reservationRepository.save(reservation)).willReturn(reservation);

        ReservationResponse expectedResponse = ReservationResponse.builder().build();
        given(reservationMapper.mapToReservationResponse(reservation)).willReturn(expectedResponse);

        ReservationResponse actualResponse = reservationCommandService.createTemporaryReservation(request);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("전화번호로 유저 연동 및 예약 확정 성공")
    void linkReservationByPhone_success() {
        String inputPhone = user.getPhone();

        Reservation temporaryReservation = ReservationFixture.미확정된_예약(conference, List.of(session), user.getName(), user.getPhone(), null);
        temporaryReservation = spy(temporaryReservation);

        given(reservationRepository.findAllByPhoneAndStatus(inputPhone, ReservationStatus.TEMPORARY)).willReturn(List.of(temporaryReservation));
        given(userContext.findByPhoneOrThrow(inputPhone)).willReturn(user);
        doNothing().when(reservationValidator).validateReservations(List.of(temporaryReservation));
        given(reservationRepository.save(any())).willReturn(temporaryReservation);

        ReservationResponse expectedResponse = ReservationResponse.builder().build();
        given(reservationMapper.mapToReservationResponse(any())).willReturn(expectedResponse);

        ReservationResponse actualResponse = reservationCommandService.linkReservationByPhone(inputPhone);

        verify(temporaryReservation, times(1)).linkUser(user);
        verify(temporaryReservation, times(1)).confirm();
        verify(reservationRepository, times(1)).save(any(Reservation.class));

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("예약 생성 실패 - Invalid Reservation Request")
    void createReservation_fail() {
        ReservationRequest request = ReservationRequest.builder()
                .conferenceId(null)
                .sessionIds(List.of())
                .name(null)
                .phone(null)
                .build();

        doThrow(new IllegalArgumentException("잘못된 요청")).when(reservationValidator).validateRequest(request);

        try {
            reservationCommandService.createReservation(request);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("잘못된 요청");
        }

        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("전화번호로 유저 연동 실패 - 예약 정보 없음")
    void linkReservationByPhone_fail_noReservation() {
        String inputPhone = user.getPhone();

        given(reservationRepository.findAllByPhoneAndStatus(inputPhone, ReservationStatus.TEMPORARY)).willReturn(List.of());

        doThrow(new IllegalArgumentException("예약 정보 없음")).when(reservationValidator).validateReservations(List.of());

        try {
            reservationCommandService.linkReservationByPhone(inputPhone);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("예약 정보 없음");
        }

        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
