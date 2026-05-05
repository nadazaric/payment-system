package com.sep.web_shop.back.feature_reservation.dto;

import com.sep.web_shop.back.feature_reservation.enumeration.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservationDetailsDTO(
        Long id,
        Long vehicleId,
        LocalDate startDate,
        LocalDate endDate,
        Long insurancePackageId,
        List<Long> additionalServiceIds,
        BigDecimal totalPrice,
        ReservationStatus status
) {
}
