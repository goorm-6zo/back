package goorm.back.zo6.reservation.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.application.ConferenceSimpleResponse;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ReservationResponse createReservation(ReservationRequest reservationRequest) {

        validateRequest(reservationRequest);

        Conference conference = conferenceJpaRepository.findById(reservationRequest.getConferenceId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));

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
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
    }

    private Set<Session> validateSessionReservations(Conference conference, List<Long> sessionIds, String name, String phone) {

        if (!conference.getHasSessions() && !sessionIds.isEmpty()) {
            throw new CustomException(ErrorCode.CONFERENCE_HAS_NO_SESSION);
        }

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
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    @Override
    public List<ReservationResponse> getMyReservations() {
        User user = userRepository.findByEmail(getCurrentUserEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Reservation> reservations = reservationRepository.findAllByUser(user);

        return reservations.stream()
                .map(this::mapToReservationResponse)
                .sorted(Comparator.comparing((ReservationResponse res) -> res.getConference().getConferenceAt()).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<ConferenceSimpleResponse> getMyConferenceSimpleList() {
        User currentUser = userRepository.findByEmail(getCurrentUserEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Reservation> reservations = reservationRepository.findAllByUser(currentUser);

        return reservations.stream()
                .map(res -> new ConferenceSimpleResponse(
                        res.getConference().getId(),
                        res.getConference().getName(),
                        res.getConference().getConferenceAt(),
                        res.getConference().getLocation()
                ))
                .sorted(Comparator.comparing(ConferenceSimpleResponse::getConferenceAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponse getReservationDetailsById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        String currentUser = getCurrentUserName();
        String currentPhone = getCurrentUserPhone();

        if (!reservation.getName().equals(currentUser) || !reservation.getPhone().equals(currentPhone)) {
            throw new IllegalStateException("조회 권한이 없습니다.");
        }

        return mapToReservationResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse createTemporaryReservation(ReservationRequest reservationRequest) {

        validateRequest(reservationRequest);

        Conference conference = conferenceJpaRepository.findById(reservationRequest.getConferenceId()).orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));

        Set<Session> validateSessions = validateSessionReservations(
                conference,
                reservationRequest.getSessionIds(),
                reservationRequest.getName(),
                reservationRequest.getPhone()
        );

        Reservation reservation = Reservation.builder()
                .conference(conference)
                .name(reservationRequest.getName())
                .phone(reservationRequest.getPhone())
                .status(ReservationStatus.TEMPORARY)
                .user(null)
                .build();

        validateSessions.forEach(session -> reservation.addSession(session));

        reservationRepository.save(reservation);

        return mapToReservationResponse(reservation);
    }

    @Transactional
    @Override
    public ReservationResponse linkReservationByPhoneAndUser(String inputPhone, Long userId) {
        List<Reservation> reservations = reservationRepository.findAllByPhoneAndStatus(inputPhone, ReservationStatus.TEMPORARY);

        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_PHONE);
        }
        Reservation reservation = reservations.get(0);

        reservation.linkUser(user);
        reservation.confirm();

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToReservationResponse(savedReservation);
    }

    @Transactional
    public void confirmUserReservationsAfterLogin(String name, String phone) {
        List<Reservation> reservations = reservationRepository.findAllByNameAndPhone(name, phone);

        reservations.stream()
                .filter(reservation -> reservation.getStatus().equals(ReservationStatus.TEMPORARY))
                .forEach(Reservation::confirmReservation);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private String getCurrentUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getCurrentUserPhone() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getPhone();
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        Conference conference = reservation.getConference();

        List<ReservationResponse.SessionInfo> sessionInfos = reservation.getReservationSessions().stream()
                .map(reservationSession -> {
                    Session session = reservationSession.getSession();
                    return ReservationResponse.SessionInfo.builder()
                            .sessionId(session.getId())
                            .conferenceId(reservation.getConference().getId())
                            .sessionName(session.getName())
                            .capacity(session.getCapacity())
                            .location(session.getLocation())
                            .time(session.getTime())
                            .summary(session.getSummary())
                            .build();
                })
                .collect(Collectors.toList());

        ReservationResponse.ConferenceInfo conferenceInfo = ReservationResponse.ConferenceInfo.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .conferenceAt(conference.getConferenceAt())
                .capacity(conference.getCapacity())
                .hasSessions(conference.getHasSessions())
                .build();

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .conference(conferenceInfo)
                .sessions(sessionInfos)
                .status(reservation.getStatus())
                .build();
    }
}