package goorm.back.zo6.reservation.presnetation;

import goorm.back.zo6.conference.application.ConferenceSimpleResponse;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.application.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "reservation", description = "Reservation API")
@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    @Operation(summary = "예약 생성", description = "예약을 생성합니다.")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "내 예약 조회", description = "로그인한 사용자의 모든 예약을 조회합니다.")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        List<ReservationResponse> reservations = reservationService.getMyReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my/conference")
    @Operation(summary = "내가 예약한 컨퍼런스 목록 조회", description = "로그인한 사용자가 예약한 컨퍼런스의 간략한 정보 목록을 조회합니다.")
    public ResponseEntity<List<ConferenceSimpleResponse>> getMyConference() {
        List<ConferenceSimpleResponse> simpleResponses = reservationService.getMyConferenceSimpleList();
        return ResponseEntity.ok(simpleResponses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "예약 상세 조회", description = "예약 ID를 통해 특정 예약의 상세 정보를 조회합니다.")
    public ResponseEntity<ReservationResponse> getReservationDetails(@PathVariable Long id) {
        ReservationResponse resDetail = reservationService.getReservationDetailsById(id);
        return ResponseEntity.ok(resDetail);
    }

    @PostMapping("/temp")
    @Operation(summary = "임시 예약 생성", description = "임시로 예약 정보를 생성합니다.")
    public ResponseEntity<ReservationResponse> createTemporaryReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createTemporaryReservation(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/link-user")
    @Operation(summary = "예약과 사용자 연결", description = "특정 예약(reservationId)의 사용자를 연결합니다. 전화번호(phone)를 통해 사용자를 검증합니다.")
    public ResponseEntity<ReservationResponse> linkUserToReservation(
            @RequestParam("phone") String inputPhone,
            @RequestParam("userId") Long userId
    ) {
        ReservationResponse response = reservationService.linkReservationByPhoneAndUser(inputPhone, userId);
        return ResponseEntity.ok(response);
    }
}
