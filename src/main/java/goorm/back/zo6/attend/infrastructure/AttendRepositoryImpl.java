package goorm.back.zo6.attend.infrastructure;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.AttendDataDto;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static goorm.back.zo6.conference.domain.QSession.session;
import static goorm.back.zo6.reservation.domain.QReservation.reservation;
import static goorm.back.zo6.reservation.domain.QReservationSession.reservationSession;

@Repository
@RequiredArgsConstructor
public class AttendRepositoryImpl implements AttendRepository {
    private final AttendJpaRepository attendJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public AttendDataDto findAttendInfo(String phone, Long conferenceId, Long sessionId) {
        return queryFactory
                .select(Projections.constructor(
                        AttendDataDto.class,
                        reservation.id,
                        reservationSession.id,
                        session.id,
                        reservation.conference.id
                ))
                .from(reservation)
                .leftJoin(reservation.reservationSessions, reservationSession)
                .leftJoin(reservationSession.session, session)
                .where(
                        reservation.phone.eq(phone),
                        reservation.conference.id.eq(conferenceId),
                        sessionId != null ? session.id.eq(sessionId) : null
                )
                .fetchOne();
    }

    @Override
    public Attend save(Attend attend) {
        return attendJpaRepository.save(attend);
    }

    @Override
    public List<Tuple> findAttendInfoByUserAndConference(Long userId, Long conferenceId) {
        return attendJpaRepository.findAttendInfoByUserAndConference(userId,conferenceId);
    }

    @Override
    public List<Tuple> findAttendData(String phone, Long conferenceId, Long sessionId) {
        return attendJpaRepository.findAttendData(phone, conferenceId, sessionId);
    }

    @Override
    public List<Tuple> findUsersWithAttendanceAndMeta(Long conferenceId, Long sessionId) {
        return attendJpaRepository.findUsersWithAttendanceAndMeta(conferenceId, sessionId);
    }
}
