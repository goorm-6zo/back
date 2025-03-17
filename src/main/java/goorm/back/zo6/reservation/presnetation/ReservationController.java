package goorm.back.zo6.reservation.presnetation;

import goorm.back.zo6.conference.application.ConferenceSimpleResponse;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.application.ReservationResponse;
import goorm.back.zo6.reservation.application.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/create")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        List<ReservationResponse> reservations = reservationService.getMyReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my/conference")
    public ResponseEntity<List<ConferenceSimpleResponse>> getMyConference() {
        List<ConferenceSimpleResponse> simpleResponses = reservationService.getMyConferenceSimpleList();
        return ResponseEntity.ok(simpleResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationDetails(@PathVariable Long id) {
        ReservationResponse resDetail = reservationService.getReservationDetailsById(id);
        return ResponseEntity.ok(resDetail);
    }

    @PostMapping("/temp")
    public ResponseEntity<ReservationResponse> createTemporaryReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createTemporaryReservation(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{reservationId}/link-user")
    public ResponseEntity<ReservationResponse> linkUserToReservation(
            @PathVariable Long reservationId,
            @RequestParam("phone") String inputPhone,
            @RequestParam("userId") Long userId
    ) {
        ReservationResponse response = reservationService.linkBeservationWithUser(reservationId, inputPhone, userId);
        return ResponseEntity.ok(response);
    }
}
