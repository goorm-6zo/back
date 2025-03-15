package goorm.back.zo6.common.event;

import goorm.back.zo6.eventstore.api.EventStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventStoreHandler {
    private EventStore eventStore;

    public EventStoreHandler(EventStore eventStore){
        this.eventStore = eventStore;
    }

    @EventListener(Event.class)
    public void handle(Event event){
        eventStore.save(event);
    }
}
