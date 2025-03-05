package goorm.back.zo6.reservation.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.DTO.request.ReservationRequest;
import goorm.back.zo6.reservation.DTO.response.ReservationResponse;
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

        if (reservationRepository.existsByConferenceIdAndNameAndPhone(
                conference.getId(),
                reservationRequest.getName(),
                reservationRequest.getPhone()
        )) {
            throw new IllegalArgumentException("이미 해당 컨퍼런스를 예약하셨습니다.");
        }

        Reservation reservation = new Reservation(conference, reservationRequest.getName(), reservationRequest.getPhone());

        List<Long> sessionIds = reservationRequest.getSessionIds();
        if (sessionIds != null && !sessionIds.isEmpty()) {
            Set<Session> reservedSessions = validateSessionReservations(conference, sessionIds, reservationRequest.getName(),reservationRequest.getPhone());

            reservedSessions.forEach(reservation::addSession);
        }

        reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .success(true)
                .message("Conferences and Sessions are successfully reserved.")
                .reservedConferenceId(conference.getId())
                .reservedSessionIds(sessionIds)
                .build();
    }

    private void validateRequest(ReservationRequest reservationRequest) {
        if (reservationRequest.getName() == null || reservationRequest.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required.");
        }
        if (reservationRequest.getPhone() == null || reservationRequest.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone is required.");
        }
        if (reservationRequest.getConferenceId() == null) {
            throw new IllegalArgumentException("Conference Id is required.");
        }
    }

    private Set<Session> validateSessionReservations(Conference conference,List<Long> requestedSessionIds,String name,String phone) {
        Set<Session> conferenceSessions = conference.getSessions();

        return requestedSessionIds.stream()
                .map(sessionId -> {
                    Session session = conferenceSessions.stream()
                            .filter(s -> s.getId().equals(sessionId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Session not found"));
                    if (reservationSessionJpaRepository.existsBySessionIdAndNameAndPhone(sessionId,name,phone)) {
                        throw new IllegalArgumentException("해당 세션을 이미 예약 하셨습니다. " + session.getName());
                    }
                    return session;
                }).collect(Collectors.toSet());

    }
}