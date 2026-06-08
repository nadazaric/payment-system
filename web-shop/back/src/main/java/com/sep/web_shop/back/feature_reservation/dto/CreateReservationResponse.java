package com.sep.web_shop.back.feature_reservation.dto;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;

public record CreateReservationResponse(
        Long reservationId,
        String redirectUrl,
        PaymentStatus paymentStatus
) {
}