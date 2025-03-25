package goorm.back.zo6.reservation.presnetation;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.reservation.application.ReservationConferenceDetailResponse;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.application.command.ReservationCommandService;
import goorm.back.zo6.reservation.application.query.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "reservation", description = "Reservation API")
@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationQueryService reservationQueryService;

    private final ReservationCommandService reservationCommandService;

    @GetMapping("/my")
    @Operation(summary = "내 예약 조회", description = "로그인한 사용자의 모든 예약을 조회합니다.")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        List<ReservationResponse> reservations = reservationQueryService.getMyReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my/conference")
    @Operation(summary = "내가 예약한 컨퍼런스 목록 조회", description = "로그인한 사용자가 예약한 컨퍼런스의 간략한 정보 목록을 조회합니다.")
    public ResponseEntity<List<ConferenceResponse>> getMyConference() {
        List<ConferenceResponse> simpleResponses = reservationQueryService.getMyConferenceSimpleList();
        return ResponseEntity.ok(simpleResponses);
    }

    @GetMapping("/my/conference/{conferenceId}")
    @Operation(summary = "예약한 특정 컨퍼런스 상세 정보 조회", description = "예약한 특정 컨퍼런스와 세션들 상세 정보를 조회합니다.")
    public ResponseEntity<ReservationConferenceDetailResponse> getReservedConferenceDetails(@PathVariable Long conferenceId) {
        ReservationConferenceDetailResponse details = reservationQueryService.getReservedConferenceDetails(conferenceId);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/{id}")
    @Operation(summary = "예약 상세 조회", description = "예약 ID를 통해 특정 예약의 상세 정보를 조회합니다.")
    public ResponseEntity<ReservationResponse> getReservationDetails(@PathVariable Long id) {
        ReservationResponse resDetail = reservationQueryService.getReservationDetailsById(id);
        return ResponseEntity.ok(resDetail);
    }

    @PostMapping("/temp")
    @Operation(summary = "임시 예약 생성", description = "임시로 예약 정보를 생성합니다.")
    public ResponseEntity<ReservationResponse> createTemporaryReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationCommandService.createTemporaryReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/link-user")
    @Operation(summary = "예약과 사용자 연결", description = "전화번호(phone)를 통해 사용자를 검증합니다.")
    public ResponseEntity<ReservationResponse> linkUserToReservation(
            @RequestParam("phone") String inputPhone
    ) {
        ReservationResponse response = reservationCommandService.linkReservationByPhone(inputPhone);
        return ResponseEntity.ok(response);
    }
}
