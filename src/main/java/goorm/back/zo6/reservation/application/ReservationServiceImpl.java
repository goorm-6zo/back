package goorm.back.zo6.reservation.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.conference.application.ConferenceSimpleResponse;
import goorm.back.zo6.conference.application.SessionDto;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.reservation.domain.ReservationSession;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class ReservationServiceImpl implements ReservationService {

    private static final String S3_BASE_URL = "https://maskpass-bucket.s3.ap-northeast-2.amazonaws.com/";

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

        if (sessionIds != null && !sessionIds.isEmpty()) {
            conference.validateSessionOwnership(Set.copyOf(sessionIds));
        }

        if (sessionIds != null && sessionIds.isEmpty()) { return Set.of(); }

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
                .sorted(Comparator.comparing((ReservationResponse res) -> res.getConference().getStartTime()).reversed())
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
                        res.getConference().getStartTime(),
                        res.getConference().getEndTime(),
                        res.getConference().getImageKey(),
                        res.getConference().getLocation()
                ))
                .sorted(Comparator.comparing(ConferenceSimpleResponse::getStartTime).reversed())
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
    public ReservationConferenceDetailResponse getReservedConferenceDetails(Long conferneceId) {
        String currentUserEmail = getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Conference conference = conferenceJpaRepository.findById(conferneceId).orElseThrow(() -> new CustomException(ErrorCode.CONFERENCE_NOT_FOUND));

        List<Reservation> reservations = reservationRepository.findByConferenceIdAndUserId(conferneceId, currentUser.getId());

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        Set<SessionDto> reservedSessions = reservations.stream()
                .flatMap(reservation -> reservation.getReservationSessions().stream())
                .map(ReservationSession::getSession)
                .map(session -> SessionDto.builder()
                        .id(session.getId())
                        .conferenceId(session.getConference().getId())
                        .name(session.getName())
                        .capacity(session.getCapacity())
                        .location(session.getLocation())
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                        .summary(session.getSummary())
                        .speakerName(session.getSpeakerName())
                        .speakerOrganization(session.getSpeakerOrganization())
                        .speakerImage(session.getSpeakerImageKey())
                        .isActive(true)
                        .build())
                .collect(Collectors.toSet());

        return ReservationConferenceDetailResponse.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .conferenceLocation(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .conferenceDescription(conference.getDescription())
                .sessions(new ArrayList<>(reservedSessions))
                .build();
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

        validateSessions.forEach(reservation::addSession);

        reservationRepository.save(reservation);

        return mapToReservationResponse(reservation);
    }

    @Transactional
    @Override
    public ReservationResponse linkReservationByPhone(String inputPhone) {
        List<Reservation> reservations = reservationRepository.findAllByPhoneAndStatus(inputPhone, ReservationStatus.TEMPORARY);

        User user = userRepository.findByPhone(inputPhone).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (reservations.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_PHONE);
        }

        Reservation reservation = reservations.get(0);

        reservation.linkUser(user);
        reservation.confirm();

        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToReservationResponse(savedReservation);
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
                            .startTime(session.getStartTime())
                            .endTime(session.getEndTime())
                            .summary(session.getSummary())
                            .speaker(session.getSpeakerName())
                            .speakerOrganization(session.getSpeakerOrganization())
                            .imageUrl(session.getSpeakerImageKey())
                            .build();
                })
                .collect(Collectors.toList());

        ReservationResponse.ConferenceInfo conferenceInfo = ReservationResponse.ConferenceInfo.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .capacity(conference.getCapacity())
                .hasSessions(conference.getHasSessions())
                .imageUrl(S3_BASE_URL + reservation.getConference().getImageKey())
                .build();

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .conference(conferenceInfo)
                .sessions(sessionInfos)
                .status(reservation.getStatus())
                .build();
    }
}