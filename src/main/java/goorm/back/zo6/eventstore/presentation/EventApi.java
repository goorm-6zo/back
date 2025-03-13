package goorm.back.zo6.eventstore.presentation;

import goorm.back.zo6.eventstore.api.EventEntry;
import goorm.back.zo6.eventstore.api.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventApi {
    private final EventStore eventStore;

    @RequestMapping(value = "/api/v1/events", method = RequestMethod.GET)
    public List<EventEntry> list(
            @RequestParam("offset") Long offset,
            @RequestParam("limit") Long limit){
        return eventStore.get(offset,limit);
    }

}
