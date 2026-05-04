package com.sep.web_shop.back.feature_reservation.controller;

import com.sep.web_shop.back.feature_reservation.dto.UnavailablePeriodDTO;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/vehicles/{vehicleId}/unavailable-periods")
    public ResponseEntity<List<UnavailablePeriodDTO>> getUnavailablePeriods(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(reservationService.getUnavailablePeriods(vehicleId));
    }

}
