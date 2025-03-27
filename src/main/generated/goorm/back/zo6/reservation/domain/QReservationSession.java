package goorm.back.zo6.reservation.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReservationSession is a Querydsl query type for ReservationSession
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReservationSession extends EntityPathBase<ReservationSession> {

    private static final long serialVersionUID = -1385937750L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReservationSession reservationSession = new QReservationSession("reservationSession");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QReservation reservation;

    public final goorm.back.zo6.conference.domain.QSession session;

    public QReservationSession(String variable) {
        this(ReservationSession.class, forVariable(variable), INITS);
    }

    public QReservationSession(Path<? extends ReservationSession> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReservationSession(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReservationSession(PathMetadata metadata, PathInits inits) {
        this(ReservationSession.class, metadata, inits);
    }

    public QReservationSession(Class<? extends ReservationSession> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.reservation = inits.isInitialized("reservation") ? new QReservation(forProperty("reservation"), inits.get("reservation")) : null;
        this.session = inits.isInitialized("session") ? new goorm.back.zo6.conference.domain.QSession(forProperty("session"), inits.get("session")) : null;
    }

}

