package com.sep.web_shop.back.feature_reservation.dto;

import java.time.LocalDate;

public record UnavailablePeriodDTO(
        LocalDate startDate,
        LocalDate endDate
) {
}