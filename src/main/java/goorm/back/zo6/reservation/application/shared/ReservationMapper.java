package goorm.back.zo6.reservation.application.shared;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.shared.ConferenceMapper;
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

    private final ConferenceMapper conferenceMapper;

    public List<ReservationResponse> mapAndSortReservations(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::mapToReservationResponse)
                .sorted(Comparator.comparing((ReservationResponse res) -> res.conference().startTime()).reversed())
                .collect(Collectors.toList());
    }

    public List<ConferenceResponse> mapToConferenceSimpleResponse(List<Reservation> reservations) {

        return reservations.stream()
                .map(this::createConferenceSimpleResponse)
                .sorted(Comparator.comparing(ConferenceResponse::startTime).reversed())
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

        List<ReservationResponse.SessionInfo> sessionInfos = reservation.getReservationSessions().stream()
                .map(rs -> mapSessionInfo(rs.getSession()))
                .sorted(Comparator.comparing(ReservationResponse.SessionInfo::startTime))
                .toList();

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .conference(mapToConferenceInfo(reservation.getConference()))
                .sessions(sessionInfos)
                .status(reservation.getStatus())
                .build();
    }

    private ReservationResponse.ConferenceInfo mapToConferenceInfo(Conference conference) {

        String imageUrl = conferenceMapper.toConferenceResponse(conference).imageUrl();

        return ReservationResponse.ConferenceInfo.builder()
                .conferenceId(conference.getId())
                .conferenceName(conference.getName())
                .description(conference.getDescription())
                .location(conference.getLocation())
                .startTime(conference.getStartTime())
                .endTime(conference.getEndTime())
                .capacity(conference.getCapacity())
                .hasSessions(conference.getHasSessions())
                .imageUrl(imageUrl)
                .build();
    }

    private ReservationResponse.SessionInfo mapSessionInfo(Session session) {

        String sessionSpeakerUrl = conferenceMapper.toSessionDto(session).speakerImage();

        return ReservationResponse.SessionInfo.builder()
                .sessionId(session.getId())
                .conferenceId(session.getConference().getId())
                .sessionName(session.getName())
                .capacity(session.getCapacity())
                .location(session.getLocation())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .summary(session.getSummary())
                .speaker(session.getSpeakerName())
                .speakerOrganization(session.getSpeakerOrganization())
                .imageUrl(sessionSpeakerUrl)
                .build();
    }

    public ConferenceResponse createConferenceSimpleResponse(Reservation reservation) {

        return conferenceMapper.toConferenceSimpleResponse(reservation.getConference());
    }
}
