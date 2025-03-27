package goorm.back.zo6.attend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAttend is a Querydsl query type for Attend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttend extends EntityPathBase<Attend> {

    private static final long serialVersionUID = 499044920L;

    public static final QAttend attend = new QAttend("attend");

    public final NumberPath<Long> conferenceId = createNumber("conferenceId", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> reservationId = createNumber("reservationId", Long.class);

    public final NumberPath<Long> reservationSessionId = createNumber("reservationSessionId", Long.class);

    public final NumberPath<Long> sessionId = createNumber("sessionId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QAttend(String variable) {
        super(Attend.class, forVariable(variable));
    }

    public QAttend(Path<? extends Attend> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAttend(PathMetadata metadata) {
        super(Attend.class, metadata);
    }

}

