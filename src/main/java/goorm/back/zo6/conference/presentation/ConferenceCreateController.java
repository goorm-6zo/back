package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.ConferenceCreateRequest;
import goorm.back.zo6.conference.application.ConferenceCreateService;
import goorm.back.zo6.conference.application.ConferenceResponse;
import goorm.back.zo6.conference.domain.Conference;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conferences")
@RequiredArgsConstructor
public class ConferenceCreateController {

    private final ConferenceCreateService conferenceCreateService;

    @PostMapping
    public ConferenceResponse createConference(@Valid @RequestBody ConferenceCreateRequest request) {
        return conferenceCreateService.createConference(request);
    }
}
