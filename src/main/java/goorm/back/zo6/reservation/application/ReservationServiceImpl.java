package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConferenceJpaRepository conferenceJpaRepository;

    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {

        validateRequest(reservationRequest);

        Conference conference = conferenceJpaRepository.findById(reservationRequest.getConferenceId())
                .orElseThrow(() -> new IllegalArgumentException("Conference not found"));

        Set<Session> reservedSessions = validateSessionReservations(
                conference,
                reservationRequest.getSessionIds(),
                reservationRequest.getName(),
                reservationRequest.getPhone()
        );

        Reservation reservation = createReservationEntity(conference, reservationRequest);
        reservedSessions.forEach(reservation::addSession);

        Reservation savedReservation = reservationRepository.save(reservation);
        return mapToReservationResponse(savedReservation);
    }

    private void validateRequest(ReservationRequest reservationRequest) {
        if (reservationRequest.getConferenceId() == null || reservationRequest.getName() == null || reservationRequest.getPhone() == null) {
            throw new IllegalArgumentException("Invalid ReservationRequest Required fields are missing");
        }
    }

    private Set<Session> validateSessionReservations(Conference conference, List<Long> sessionIds, String name, String phone) {

        if (!sessionIds.isEmpty()) {
            conference.validateSessionOwnership(Set.copyOf(sessionIds));
        }

        if (sessionIds.isEmpty()) { return Set.of(); }

        return conference.getSessions().stream()
                .filter(session -> sessionIds.contains(session.getId()))
                .filter(Session::isReservable)
                .collect(Collectors.toSet());
    }

    private Reservation createReservationEntity(Conference conference, ReservationRequest reservationRequest) {
        return Reservation.builder()
                .conference(conference)
                .name(reservationRequest.getName())
                .phone(reservationRequest.getPhone())
                .build();
    }

    @Override
    public List<ReservationResponse> getMyReservations() {
        String userName = getCurrentUserName();
        String userPhone = getCurrentUserPhone();

        List<Reservation> reservations = reservationRepository.findAllByNameAndPhone(userName, userPhone);

        return reservations.stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    private String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentUserPhone() {
        return "010-1234-5678"; // 테스트용 하드 코딩
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .success(true)
                .message("Reservation successfully created")
                .reservedConferenceId(reservation.getConference().getId())
                .reservedSessionIds(
                        reservation.getReservationSessions().stream()
                                .map(rs -> rs.getSession().getId())
                                .collect(Collectors.toList())
                )
                .build();
    }
}