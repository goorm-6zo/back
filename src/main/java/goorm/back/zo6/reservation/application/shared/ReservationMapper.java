package goorm.back.zo6.reservation.application.shared;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.application.ReservationConferenceDetailResponse;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.domain.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private static final String S3_BASE_URL = "https://maskpass-bucket.s3.ap-northeast-2.amazonaws.com/";

    public List<ReservationResponse> mapAndSortReservations(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::mapToReservationResponse)
                .sorted(Comparator.comparing((ReservationResponse res) -> res.getConference().getStartTime()).reversed())
                .collect(Collectors.toList());
    }

    public List<ConferenceResponse> mapToConferenceSimpleResponse(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::createConferenceSimpleResponse)
                .sorted(Comparator.comparing(ConferenceResponse::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    public ReservationConferenceDetailResponse mapToDetailResponse(Conference conference, List<SessionDto> reservedSessions) {
        return ReservationConferenceDetailResponse.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .conferenceLocation(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .conferenceDescription(conference.getDescription())
                .sessions(reservedSessions)
                .build();
    }

    public ReservationResponse mapToReservationResponse(Reservation reservation) {
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

    public ConferenceResponse createConferenceSimpleResponse(Reservation reservation) {
        return ConferenceResponse.simpleFrom(
                reservation.getConference(), S3_BASE_URL + reservation.getConference().getImageKey()
        );
    }
}
