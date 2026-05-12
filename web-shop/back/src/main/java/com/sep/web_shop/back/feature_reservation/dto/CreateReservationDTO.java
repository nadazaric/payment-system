package com.sep.web_shop.back.feature_reservation.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateReservationDTO(
        Long vehicleId,
        LocalDate startDate,
        LocalDate endDate,
        Long insurancePackageId,
        List<Long> additionalServiceIds
) {
}
