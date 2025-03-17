package goorm.back.zo6.attend.presentation;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.dto.AttendInfoResponse;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.auth.domain.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Attend", description = "Attend API")
@RequestMapping("/api/v1/attend")
@RestController
@RequiredArgsConstructor
public class AttendController {
    private final AttendService attendService;
    @GetMapping()
    @Operation(summary = "유저의 행사 참가 내역을 확인", description = "유저의 행사 참가 내역을 조회합니다.")
    public ResponseEntity<List<ConferenceInfoDto>> findByToken(@AuthenticationPrincipal LoginUser loginUser,
                                                                @RequestParam("conferenceId")Long conferenceId){
        Long userId = loginUser.user().getId();
        List<ConferenceInfoDto> attendResponses = attendService.findAllByToken(userId,conferenceId);
        return ResponseEntity.ok(attendResponses);
    }

    @DeleteMapping
    @Operation(summary = "유저의 참가 내역 삭제", description = "유저의 행사 참가 내역을 삭제합니다.")
    public ResponseEntity<Map<String,String>> deleteByToken(@AuthenticationPrincipal LoginUser loginUser,
                                                            @RequestParam("reservationId") Long reservationId,
                                                            @RequestParam("reservationSessionId") Long reservationSessionId){
        Long userId = loginUser.user().getId();
        attendService.deleteByToken(userId,reservationId, reservationSessionId);
        return ResponseEntity.ok(Map.of("message","유저의 행사 참가 내역 삭제 완료!"));
    }
}
