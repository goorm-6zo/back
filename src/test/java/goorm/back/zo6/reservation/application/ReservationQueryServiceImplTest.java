package goorm.back.zo6.reservation.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.application.shared.SessionFactory;
import goorm.back.zo6.conference.application.shared.SessionValidator;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.ReservationFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.fixture.UserFixture;
import goorm.back.zo6.reservation.application.query.ReservationQueryServiceImpl;
import goorm.back.zo6.reservation.application.shared.ReservationMapper;
import goorm.back.zo6.reservation.application.shared.ReservationValidator;
import goorm.back.zo6.reservation.application.shared.UserContext;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class ReservationQueryServiceImplTest {

    @Mock
    private ConferenceValidator conferenceValidator;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationValidator reservationValidator;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private SessionValidator sessionValidator;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ReservationQueryServiceImpl reservationQueryService;

    private User user;

    private Conference conference;

    private Session session;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = UserFixture.유저();
        conference = ConferenceFixture.컨퍼런스_아이디포함();
        session = SessionFixture.세션_아이디포함(conference);
        reservation = ReservationFixture.확정된예약(conference, List.of(session),  user.getName(), user.getPhone(), user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    @DisplayName("예약 상세 조회 성공")
    void getReservationDetailsById_Success() {
        Long reservationId = 1L;

        given(reservationValidator.getReservationOrThrow(reservationId)).willReturn(reservation);
        given(userContext.getCurrentUserName()).willReturn(user.getName());
        given(userContext.getCurrentUserPhone()).willReturn(user.getPhone());

        ReservationResponse expected = ReservationResponse.builder().build();
        given(reservationMapper.mapToReservationResponse(reservation)).willReturn(expected);

        ReservationResponse actual = reservationQueryService.getReservationDetailsById(reservationId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("내 예약 목록 조회 성공")
    void getMyReservations_success() {
        List<Reservation> reservations = List.of(reservation);
        List<ReservationResponse> reseponses = List.of(ReservationResponse.builder().build());

        given(userContext.getCurrentUserId()).willReturn(user.getId());
        given(reservationRepository.findAllByUserId(user.getId())).willReturn(reservations);
        given(reservationMapper.mapAndSortReservations(reservations)).willReturn(reseponses);

        List<ReservationResponse> actual = reservationQueryService.getMyReservations();

        assertThat(actual).isEqualTo(reseponses);
    }

    @Test
    @DisplayName("내 컨퍼런스 간단 목록 조회 성공")
    void getMyConferencesSimpleList_success() {

        List<Reservation> reservations = List.of(reservation);
        List<ConferenceResponse> responses = List.of(ConferenceResponse.builder().build());

        given(userContext.getCurrentUserId()).willReturn(user.getId());
        given(reservationRepository.findAllByUserId(user.getId())).willReturn(reservations);
        given(reservationMapper.mapToConferenceSimpleResponse(reservations)).willReturn((responses));

        List<ConferenceResponse> actual = reservationQueryService.getMyConferenceSimpleList();

        assertThat(actual).isEqualTo(responses);
    }

    @Test
    @DisplayName("예약된 컨퍼런스 상세 조회 성공")
    void getReservedConferenceDetails_success() {
        Long conferenceId = conference.getId();
        Set<SessionDto> sessionsDtos = Set.of(SessionDto.builder().build());
        ReservationConferenceDetailResponse expectedResepnse = ReservationConferenceDetailResponse.builder().build();

        given(userContext.getCurrentUserEmail()).willReturn(user.getEmail());
        given(userContext.findByEmailOrThrow(user.getEmail())).willReturn(user);
        given(conferenceValidator.findConferenceWithSessionsOrThrow(conferenceId)).willReturn(conference);
        given(reservationRepository.findByConferenceIdAndUserId(conferenceId, user.getId())).willReturn(List.of(reservation));
        given(sessionFactory.createSessionDtos(List.of(reservation))).willReturn(sessionsDtos);
        given(reservationMapper.mapToDetailResponse(conference, List.copyOf(sessionsDtos))).willReturn(expectedResepnse);

        ReservationConferenceDetailResponse actual = reservationQueryService.getReservedConferenceDetails(conferenceId);

        assertThat(actual).isEqualTo(expectedResepnse);
    }

    @Test
    @DisplayName("예약 상세 조회 실패 - 권한 없는 사용자 접근")
    void getReservationDetailsById_fail() {
        Long reservationId = 1L;

        UsernamePasswordAuthenticationToken invalidAuthToken = new UsernamePasswordAuthenticationToken("invalidUser@gmail.com", null, Collections.emptyList() );
        SecurityContextHolder.getContext().setAuthentication(invalidAuthToken);

        given(reservationValidator.getReservationOrThrow(reservationId)).willReturn(reservation);
        given(userContext.getCurrentUserName()).willReturn("다른 사용자");
        given(userContext.getCurrentUserPhone()).willReturn("01099998888");

        willThrow(new CustomException( ErrorCode.FORBIDDEN_ACCESS )).given(reservationValidator).validateUserAccess(reservation, "다른 사용자", "01099998888");

        CustomException exception = assertThrows( CustomException.class, () -> reservationQueryService.getReservationDetailsById(reservationId) );

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ACCESS);
    }
}