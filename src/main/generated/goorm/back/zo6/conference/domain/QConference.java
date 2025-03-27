package goorm.back.zo6.conference.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConference is a Querydsl query type for Conference
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConference extends EntityPathBase<Conference> {

    private static final long serialVersionUID = 1866502076L;

    public static final QConference conference = new QConference("conference");

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final BooleanPath hasSessions = createBoolean("hasSessions");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageKey = createString("imageKey");

    public final BooleanPath isActive = createBoolean("isActive");

    public final StringPath location = createString("location");

    public final StringPath name = createString("name");

    public final ListPath<Session, QSession> sessions = this.<Session, QSession>createList("sessions", Session.class, QSession.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public QConference(String variable) {
        super(Conference.class, forVariable(variable));
    }

    public QConference(Path<? extends Conference> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConference(PathMetadata metadata) {
        super(Conference.class, metadata);
    }

}

