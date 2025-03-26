package goorm.back.zo6.reservation.application.shared;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public void validateReservations(List<Reservation> reservations) {

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    public void validateUserAccess(Reservation reservation, String currentUser, String currentPhone) {

        if (!reservation.getName().equals(currentUser) || !reservation.getPhone().equals(currentPhone)) {
            throw new IllegalStateException("조회 권한이 없습니다.");
        }
    }

    public Reservation getReservationOrThrow(Long reservationId) {

        return reservationRepository.findById(reservationId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    public void validateRequest(ReservationRequest reservationRequest) {

        if (reservationRequest.conferenceId() == null || reservationRequest.name() == null || reservationRequest.phone() == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

    public Set<Session> validateSessionReservations(Conference conference, List<Long> sessionIds, String name, String phone) {

        if (!conference.getHasSessions() && !sessionIds.isEmpty()) {
            throw new CustomException(ErrorCode.CONFERENCE_HAS_NO_SESSION);
        }

        if (sessionIds != null && !sessionIds.isEmpty()) {
            conference.validateSessionOwnership(Set.copyOf(sessionIds));
        }

        if (sessionIds != null && sessionIds.isEmpty()) { return Set.of(); }

        return conference.getSessions().stream()
                .filter(session -> sessionIds.contains(session.getId()))
                .filter(Session::isReservable)
                .collect(Collectors.toSet());
    }
}
