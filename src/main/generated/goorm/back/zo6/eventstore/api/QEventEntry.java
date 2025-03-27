package goorm.back.zo6.eventstore.api;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QEventEntry is a Querydsl query type for EventEntry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEventEntry extends EntityPathBase<EventEntry> {

    private static final long serialVersionUID = 1843613259L;

    public static final QEventEntry eventEntry = new QEventEntry("eventEntry");

    public final StringPath contentType = createString("contentType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath payload = createString("payload");

    public final NumberPath<Long> timestamp = createNumber("timestamp", Long.class);

    public final StringPath type = createString("type");

    public QEventEntry(String variable) {
        super(EventEntry.class, forVariable(variable));
    }

    public QEventEntry(Path<? extends EventEntry> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEventEntry(PathMetadata metadata) {
        super(EventEntry.class, metadata);
    }

}

