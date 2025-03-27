package goorm.back.zo6.attend.infrastructure;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.attend.dto.*;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static goorm.back.zo6.attend.domain.QAttend.attend;
import static goorm.back.zo6.conference.domain.QConference.conference;
import static goorm.back.zo6.conference.domain.QSession.session;
import static goorm.back.zo6.reservation.domain.QReservation.reservation;
import static goorm.back.zo6.reservation.domain.QReservationSession.reservationSession;
import static goorm.back.zo6.user.domain.QUser.user;


@Repository
@RequiredArgsConstructor
public class AttendRepositoryImpl implements AttendRepository {
    private final AttendJpaRepository attendJpaRepository;
    private final JPAQueryFactory queryFactory;

    // 유저의 에매 내역을 기반으로 참석 정보 Attend 를 만들기 위한 쿼리문
    @Override
    public AttendData findAttendInfo(String phone, Long conferenceId, Long sessionId) {
        List<AttendData> result = queryFactory
                .select(Projections.constructor(
                        AttendData.class,
                        reservation.id,
                        sessionId != null ? reservationSession.id : Expressions.nullExpression(Long.class),
                        reservation.conference.id,
                        sessionId != null ? session.id : Expressions.nullExpression(Long.class)
                ))
                .from(reservation)
                .leftJoin(reservation.reservationSessions, reservationSession)
                .leftJoin(reservationSession.session, session)
                .where(
                        reservation.phone.eq(phone),
                        reservation.conference.id.eq(conferenceId),
                        reservation.status.eq(ReservationStatus.CONFIRMED),
                        sessionId != null ? session.id.eq(sessionId) : null
                )
                .fetch();
        if(result.isEmpty()){
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        return result.get(0);
    }

    // 해당 유저가 예매한 컨퍼런스 및 세션의 정보 및 참석 여부를 리턴합니다.
    @Override
    public ConferenceInfoResponse findAttendInfoByUserAndConference(Long userId, Long conferenceId) {
        List<Tuple> tuples = queryFactory
                .select(
                        conference.id, conference.name, conference.description, conference.location, conference.startTime, conference.endTime,
                        conference.capacity, conference.hasSessions, conference.imageKey, conference.isActive,
                        attend.id.max().isNotNull(), // conference 참석 여부
                        session.id, session.name, session.capacity, session.location,
                        session.startTime, session.endTime, session.summary,
                        session.speakerName, session.speakerOrganization, session.speakerImageKey, session.isActive,
                        attend.sessionId.max().isNotNull() // session 참석 여부
                )
                .from(reservation)
                .join(reservation.conference, conference)
                .leftJoin(reservation.reservationSessions, reservationSession)
                .leftJoin(reservationSession.session, session)
                .leftJoin(attend).on(
                        attend.userId.eq(userId)
                                .and(attend.reservationId.eq(reservation.id))
                                .and(attend.sessionId.eq(session.id).or(attend.sessionId.isNull()))
                )
                .where(
                        reservation.phone.eq(JPAExpressions.select(user.phone).from(user).where(user.id.eq(userId))),
                        reservation.conference.id.eq(conferenceId),
                        reservation.status.eq(ReservationStatus.CONFIRMED)
                )
                .groupBy(conference.id, session.id)
                .fetch();

        return convertToConferenceInfo(tuples);
    }

    // 컨퍼런스, 세션 기준으로 유저 및 참석 상태 조회, 그리고 해당하는 행사의 메타 데이터를 리턴합니다.
    @Override
    public AttendanceSummaryQuery findAttendanceSummary(Long conferenceId, Long sessionId) {
        List<Tuple> results = queryFactory
                .select(
                        user.id,
                        user.name,
                        attend.id.count().gt(0),
                        sessionId != null ? session.name : conference.name,
                        sessionId != null ? session.capacity : conference.capacity
                )
                .from(reservation)
                .join(reservation.user, user)
                .join(reservation.conference, conference)
                .leftJoin(reservation.reservationSessions, reservationSession)
                .leftJoin(reservationSession.session, session)
                .leftJoin(attend).on(
                        attend.userId.eq(user.id)
                                .and(
                                        sessionId != null
                                                ? attend.sessionId.eq(sessionId)
                                                : attend.conferenceId.eq(conferenceId).and(attend.sessionId.isNull())
                                )
                )
                .where(
                        reservation.conference.id.eq(conferenceId),
                        reservation.status.eq(ReservationStatus.CONFIRMED),
                        sessionId != null ? session.id.eq(sessionId) : null
                )
                .groupBy(
                        user.id, user.name,
                        conference.name, conference.capacity,
                        session.name, session.capacity
                )
                .fetch();

        return convertToAttendanceSummary(results);
    }

    @Override
    public Attend save(Attend attend) {
        return attendJpaRepository.save(attend);
    }

    // 조회한 튜플을 ConferenceInfo 와 SessionInfo 로 변환합니다.
    private ConferenceInfoResponse convertToConferenceInfo(List<Tuple> tuples) {
        if (tuples.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }

        Tuple first = tuples.get(0);

        ConferenceInfoResponse conferenceInfoResponse = new ConferenceInfoResponse(
                first.get(conference.id),
                first.get(conference.name),
                first.get(conference.description),
                first.get(conference.location),
                first.get(conference.startTime),
                first.get(conference.endTime),
                first.get(conference.capacity),
                first.get(conference.hasSessions),
                first.get(conference.imageKey),
                first.get(conference.isActive),
                first.get(attend.id.max().isNotNull()),
                extractSessionInfos(tuples)
        );

        return conferenceInfoResponse;
    }

    // 조회한 튜플을 을 ConferenceInfo 에 들어갈 SessionInfo 로 변환합니다.
    private List<SessionInfo> extractSessionInfos(List<Tuple> tuples) {
        Set<Long> setSessionIds = new HashSet<>();

        return tuples.stream()
                .filter(t -> t.get(session.id) != null)
                .filter(t -> setSessionIds.add(t.get(session.id))) // 중복 제거
                .map(t -> new SessionInfo(
                        t.get(session.id),
                        t.get(session.name),
                        t.get(session.capacity),
                        t.get(session.location),
                        t.get(session.startTime),
                        t.get(session.endTime),
                        t.get(session.summary),
                        t.get(session.speakerName),
                        t.get(session.speakerOrganization),
                        t.get(session.speakerImageKey),
                        t.get(session.isActive),
                        t.get(attend.sessionId.max().isNotNull())
                ))
                .collect(Collectors.toList());
    }

    // 조회한 튜플을 컨퍼런스 혹은 세션에 참석한 유저 및 참석형태 , 메타 데이터로 변환합니다.
    private AttendanceSummaryQuery convertToAttendanceSummary(List<Tuple> results) {
        if(results.isEmpty()){
            throw new CustomException(ErrorCode.NO_ATTENDANCE_DATA);
        }

        Tuple first = results.get(0);
        String title = first.get(3, String.class);
        Integer capacity = first.get(4, Integer.class);

        List<UserAttendanceResponse> userAttendances = results.stream()
                .map(t -> UserAttendanceResponse.of(
                        t.get(0, Long.class),
                        t.get(1, String.class),
                        t.get(2, Boolean.class)
                ))
                .toList();

        return new AttendanceSummaryQuery(title, capacity, userAttendances);
    }
}
