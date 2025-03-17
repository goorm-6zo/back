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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ConferenceJpaRepository conferenceJpaRepository;

    @InjectMocks
    private ReservationServiceImpl reservationServiceImpl;

    @Test
    @DisplayName("유효한 세션을 포함하여 예약 성공")
    void createReservation_Success_WithSessions() {

        Conference conference = ConferenceFixture.컨퍼런스();
        Session session1 = SessionFixture.세션(conference);
        Session session2 = SessionFixture.세션(conference);

        setConferenceId(conference, 1L);
        setSessionId(session1, 100L);
        setSessionId(session2, 101L);

        conference.getSessions().add(session1);
        conference.getSessions().add(session2);

        ReservationRequest reservationRequest = new ReservationRequest(
                conference.getId(),
                List.of(session1.getId(), session2.getId()),
                "Test User",
                "010-1234-5678"
        );

        when(conferenceJpaRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationServiceImpl.createReservation(reservationRequest);

        assertNotNull(response);
        assertEquals(ReservationStatus.CONFIRMED, response.getStatus());
    }

    @Test
    @DisplayName("세션 없이 예약 성공")
    void createReservation_Success_WithoutSessions() {

        Conference conference = ConferenceFixture.컨퍼런스();
        setConferenceId(conference, 1L);

        ReservationRequest reservationRequest = new ReservationRequest(
                conference.getId(),
                List.of(),
                "Test User",
                "010-1234-5678"
        );

        when(conferenceJpaRepository.findById(conference.getId())).thenReturn(Optional.of(conference));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationServiceImpl.createReservation(reservationRequest);

        assertNotNull(response);

        verify(conferenceJpaRepository, times(1)).findById(conference.getId());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("유효하지 않은 세션을 포함할 경우 예약 실패")
    void createReservation_Fail_WithInvalidSessions() {

        Conference conference = ConferenceFixture.컨퍼런스();
        setConferenceId(conference, 1L);

        ReservationRequest reservationRequest = new ReservationRequest(
                conference.getId(),
                List.of(999L),
                "Test User",
                "010-1234-5678"
        );

        when(conferenceJpaRepository.findById(conference.getId())).thenReturn(Optional.of(conference));

        CustomException exception = assertThrows(CustomException.class, () -> reservationServiceImpl.createReservation(reservationRequest));

        assertEquals("This conference does not have all the sessions", exception.getMessage());

        verify(conferenceJpaRepository, times(1)).findById(conference.getId());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("존재하지 않는 컨퍼런스로 인해 예약 실패")
    void createReservation_Fail_ConferenceNotFound() {
        Long conferenceId = 1L;
        ReservationRequest reservationRequest = new ReservationRequest(
                conferenceId,
                List.of(100L, 101L),
                "Test User",
                "010-1234-5678"
        );

        when(conferenceJpaRepository.findById(conferenceId)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> reservationServiceImpl.createReservation(reservationRequest));

        assertEquals("Conference not found", exception.getMessage());
        verify(conferenceJpaRepository, times(1)).findById(conferenceId);
        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("잘못된 세션으로 인해 예약 실패")
    void createReservation_Fail_InvalidSession() {

        Conference conference = ConferenceFixture.컨퍼런스();

        setConferenceId(conference, 1L);

        ReservationRequest reservationRequest = new ReservationRequest(
                conference.getId(),
                List.of(999L),
                "Test User",
                "010-1234-5678"
        );

        when(conferenceJpaRepository.findById(conference.getId())).thenReturn(Optional.of(conference));

        CustomException exception = assertThrows(CustomException.class, () -> reservationServiceImpl.createReservation(reservationRequest));

        assertEquals("This conference does not have all the sessions", exception.getMessage());
    }

    private void setSessionId(Session session, Long id) {
        try {
            var field = Session.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(session, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set session id: ", e);
        }
    }

    private void setConferenceId(Conference conference, Long id) {
        try {
            var field = Conference.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(conference, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set conference id: ", e);
        }
    }
}