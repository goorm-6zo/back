package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.face.domain.ParticipationEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class ParticipationEventHandler {

    @TransactionalEventListener(
            classes = ParticipationEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(ParticipationEvent event){
    }
}
