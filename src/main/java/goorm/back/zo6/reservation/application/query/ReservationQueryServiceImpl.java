package goorm.back.zo6.reservation.application.query;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.application.shared.SessionFactory;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.reservation.application.ReservationConferenceDetailResponse;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.application.shared.ReservationMapper;
import goorm.back.zo6.reservation.application.shared.ReservationValidator;
import goorm.back.zo6.reservation.application.shared.UserContext;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationQueryServiceImpl implements ReservationQueryService {

    private final ReservationRepository reservationRepository;

    private final ConferenceValidator conferenceValidator;

    private final ReservationValidator reservationValidator;

    private final ReservationMapper reservationMapper;

    private final SessionFactory sessionFactory;

    private final UserContext userContext;

    @Override
    public List<ReservationResponse> getMyReservations() {

        Long userId = userContext.getCurrentUserId();
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        return reservationMapper.mapAndSortReservations(reservations);
    }

    @Override
    public List<ConferenceResponse> getMyConferenceSimpleList() {

        Long userId = userContext.getCurrentUserId();
        List<Reservation> reservations = reservationRepository.findAllByUserId(userId);

        return reservationMapper.mapToConferenceSimpleResponse(reservations);
    }

    @Override
    public ReservationResponse getReservationDetailsById(Long reservationId) {

        Reservation reservation = reservationValidator.getReservationOrThrow(reservationId);

        String currentUser = userContext.getCurrentUserName();
        String currentPhone = userContext.getCurrentUserPhone();

        reservationValidator.validateUserAccess(reservation, currentUser, currentPhone);

        return reservationMapper.mapToReservationResponse(reservation);
    }

    @Override
    public ReservationConferenceDetailResponse getReservedConferenceDetails(Long conferneceId) {

        String currentUserEmail = userContext.getCurrentUserEmail();
        User currentUser = userContext.findByEmailOrThrow(currentUserEmail);

        Conference conference = conferenceValidator.findConferenceWithSessionsOrThrow(conferneceId);
        List<Reservation> reservations = reservationRepository.findByConferenceIdAndUserId(conferneceId, currentUser.getId());
        reservationValidator.validateReservations(reservations);

        Set<SessionDto> reservedSessions = sessionFactory.createSessionDtos(reservations);

        return reservationMapper.mapToDetailResponse(conference, new ArrayList<>(reservedSessions));
    }
}
