package goorm.back.zo6.conference.application.shared;

import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SessionFactory {

    public Set<SessionDto> createSessionDtos(List<Reservation> reservations) {
        return reservations.stream()
                .flatMap(reservation -> reservation.getReservationSessions().stream())
                .map(ReservationSession::getSession)
                .map(SessionDto::fromEntity)
                .collect(Collectors.toSet());
    }

    public SessionDto convertToSessionDto(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .conferenceId(session.getConference() != null ? session.getConference().getId() : null)
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
                .build();
    }
}
