package goorm.back.zo6.reservation.presnetation;

import goorm.back.zo6.reservation.DTO.request.ReservationRequest;
import goorm.back.zo6.reservation.DTO.response.ReservationResponse;
import goorm.back.zo6.reservation.application.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);

        return ResponseEntity.status(201).body(response);
    }
}
