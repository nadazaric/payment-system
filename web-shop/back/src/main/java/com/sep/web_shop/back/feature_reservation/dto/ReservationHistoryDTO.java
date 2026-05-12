package com.sep.web_shop.back.feature_reservation.dto;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ReservationHistoryDTO(
        Long id,
        Long vehicleId,
        String vehicleName,
        String vehicleImagePath,
        String vehicleType,
        LocalDate startDate,
        LocalDate endDate,
        String insurancePackageName,
        List<String> additionalServiceNames,
        BigDecimal totalPrice,
        PaymentStatus paymentStatus
) {
}
