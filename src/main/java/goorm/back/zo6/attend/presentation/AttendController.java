package goorm.back.zo6.attend.presentation;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.auth.domain.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Attend", description = "Attend API")
@RequestMapping("/api/v1/attend")
@RestController
@RequiredArgsConstructor
public class AttendController {
    private final AttendService attendService;
    @GetMapping()
    @Operation(summary = "유저의 행사 참가 내역을 확인", description = "유저의 행사 참가 내역을 조회합니다.")
    public ResponseEntity<ConferenceInfoDto> findByToken(@AuthenticationPrincipal LoginUser loginUser,
                                                                @RequestParam("conferenceId")Long conferenceId){
        Long userId = loginUser.user().getId();
        ConferenceInfoDto attendResponses = attendService.findAllByToken(userId,conferenceId);
        return ResponseEntity.ok(attendResponses);
    }
}
