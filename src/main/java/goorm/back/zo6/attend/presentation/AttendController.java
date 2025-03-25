package goorm.back.zo6.attend.presentation;

import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.dto.AttendanceSummaryResponse;
import goorm.back.zo6.attend.dto.ConferenceInfoDto;
import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "attend", description = "Attend API")
@RequestMapping("/api/v1/attend")
@RestController
@RequiredArgsConstructor
public class AttendController {
    private final AttendService attendService;
    @GetMapping
    @Operation(summary = "유저의 행사 참가 내역 조회", description = "유저의 행사 참가 내역을 조회합니다. <br>" +
            "예매 내역 기반으로 유저의 참석 정보를 함께 조회합니다.")
    public ResponseEntity<ResponseDto<ConferenceInfoDto>> getAttendWithReservationByToken(@AuthenticationPrincipal LoginUser loginUser,
                                                                                         @RequestParam("conferenceId")Long conferenceId){
        Long userId = loginUser.getId();
        ConferenceInfoDto attendResponses = attendService.findAllByToken(userId, conferenceId);
        return ResponseEntity.ok(ResponseDto.of(attendResponses));
    }

    @GetMapping("/users")
    @Operation(summary = "컨퍼런스, 세션에 예매한 유저들의 참석 내용 조회", description = "컨퍼런스, 세션 예매한 유저들을 조회하고," +
            " 해당 유저들이 행사에 참여했는지 여부와 함께 조회합니다.")
    public ResponseEntity<ResponseDto<AttendanceSummaryResponse>> getAttendanceSummary(@RequestParam("conferenceId") Long conferenceId,
                                                                                       @RequestParam(value = "sessionId", required = false) Long sessionId){
        AttendanceSummaryResponse attendanceSummaryResponse = attendService.getAttendanceSummary(conferenceId, sessionId);
        return ResponseEntity.ok(ResponseDto.of(attendanceSummaryResponse));
    }
}
