package com.sep.web_shop.back.feature_reservation.controller;

import com.sep.web_shop.back.feature_reservation.dto.CreateReservationDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationDetailsDTO;
import com.sep.web_shop.back.feature_reservation.dto.ReservationHistoryDTO;
import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/unavailable-periods/vehicles/{vehicleId}")
    public ResponseEntity<List<UnavailablePeriodDTO>> getUnavailablePeriods(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reservationService.getUnavailablePeriods(vehicleId));
    }

    @PostMapping
    public ResponseEntity<ReservationDetailsDTO> createReservation(
            @RequestBody CreateReservationDTO createReservationDTO,
            Authentication authentication
    ) {
        Optional<ReservationDetailsDTO> reservationDetailsDTO = reservationService.createReservation(
                createReservationDTO,
                authentication.getName()
        );

        return reservationDetailsDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<List<ReservationHistoryDTO>> getReservations(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reservationService.getReservations(authentication.getName())
        );
    }

    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<List<ReservationHistoryDTO>> getReservationsByVehicle(
            @PathVariable Long vehicleId
    ) {
        return ResponseEntity.ok(
                reservationService.getReservationsByVehicle(vehicleId)
        );
    }

}
