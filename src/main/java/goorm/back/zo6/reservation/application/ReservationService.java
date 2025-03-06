package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.infrastructure.ReservationSessionJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConferenceRepository conferenceRepository;
    private final ReservationSessionJpaRepository reservationSessionJpaRepository;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {

        validateRequest(reservationRequest);

        Conference conference = conferenceRepository.findById(reservationRequest.getConferenceId())
                .orElseThrow(() -> new IllegalArgumentException("Conference not found"));

        Set<Session> reservedSessions = validateSessionReservations(
                conference,
                reservationRequest.getSessionIds(),
                reservationRequest.getName(),
                reservationRequest.getPhone()
        );

        Reservation reservation = createReservationEntity(conference, reservationRequest);
        reservedSessions.forEach(reservation::addSession);
        reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .success(true)
                .message("Reservation successfully created")
                .reservedConferenceId(conference.getId())
                .reservedSessionIds(reservedSessions.stream()
                        .map(Session::getId).collect(Collectors.toList()))
                .build();
    }

    private void validateRequest(ReservationRequest reservationRequest) {
        if (reservationRequest.getConferenceId() == null || reservationRequest.getName() == null || reservationRequest.getPhone() == null) {
            throw new IllegalArgumentException("Invalid ReservationRequest Required fields are missing");
        }
    }

    private Set<Session> validateSessionReservations(Conference conference, List<Long> sessionIds, String name, String phone) {
        conference.validateSessionOwnership(Set.copyOf(sessionIds));

        boolean duplicateReservation = sessionIds.stream()
                .anyMatch(sessionId -> reservationRepository.existsBySessionIdAndNameAndPhone(sessionId, name, phone));

        if (duplicateReservation) {
            throw new IllegalArgumentException("Duplicate reservation detected for one or more sessions");
        }

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
}