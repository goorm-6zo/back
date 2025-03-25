package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.application.command.conference.ConferenceCommandService;
import goorm.back.zo6.conference.application.dto.ConferenceCreateRequest;
import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "conference", description = "Conference API")
@RestController
@RequestMapping("/conferences")
@RequiredArgsConstructor
public class ConferenceCreateController {

    private final ConferenceCommandService conferenceCommandService;

    @PostMapping
    @Operation(summary = "컨퍼런스 생성", description = "새로운 컨퍼런스를 생성합니다.")
    public ResponseEntity<ResponseDto<ConferenceResponse>> createConference(@Valid @RequestBody ConferenceCreateRequest request) {
        ConferenceResponse conferenceResponse = conferenceCommandService.createConference(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(conferenceResponse));
    }
}
