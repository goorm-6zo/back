package goorm.back.zo6.reservation.application.command;

import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.application.shared.ReservationFactory;
import goorm.back.zo6.reservation.application.shared.ReservationMapper;
import goorm.back.zo6.reservation.application.shared.ReservationValidator;
import goorm.back.zo6.reservation.application.shared.UserContext;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationCommandServiceImpl implements ReservationCommandService {

    private final ConferenceValidator conferenceValidator;

    private final ReservationMapper reservationMapper;

    private final ReservationFactory reservationFactory;

    private final ReservationValidator reservationValidator;

    private final ReservationRepository reservationRepository;

    private final UserContext userContext;

    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {

        reservationValidator.validateRequest(reservationRequest);

        Conference conference = conferenceValidator.findConferenceOrThrow(reservationRequest.getConferenceId());

        Set<Session> sessions = reservationValidator.validateSessionReservations(conference, reservationRequest.getSessionIds(), reservationRequest.getName(), reservationRequest.getPhone());

        Reservation reservation = reservationFactory.createReservationEntity(conference, reservationRequest, sessions, ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.mapToReservationResponse(savedReservation);
    }

    @Override
    @Transactional
    public ReservationResponse createTemporaryReservation(ReservationRequest reservationRequest) {

        reservationValidator.validateRequest(reservationRequest);

        Conference conference = conferenceValidator.findConferenceWithSessionsOrThrow(reservationRequest.getConferenceId());

        Set<Session> sessions = reservationValidator.validateSessionReservations(conference, reservationRequest.getSessionIds(), reservationRequest.getName(), reservationRequest.getPhone());

        Reservation reservation = reservationFactory.createReservationEntity(conference, reservationRequest, sessions, ReservationStatus.TEMPORARY);

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.mapToReservationResponse(savedReservation);
    }

    @Transactional
    @Override
    public ReservationResponse linkReservationByPhone(String inputPhone) {

        List<Reservation> reservations = reservationRepository.findAllByPhoneAndStatus(inputPhone, ReservationStatus.TEMPORARY);

        User user = userContext.findByPhoneOrThrow(inputPhone);

        reservationValidator.validateReservations(reservations);

        Reservation reservation = reservations.get(0);

        reservation.linkUser(user);
        reservation.confirm();

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.mapToReservationResponse(savedReservation);
    }
}
