package com.sep.web_shop.back.feature_reservation.event;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResultNotificationEvent(
        String paymentId,
        String merchantId,
        String sellerReference,
        String merchantOrderId,
        PaymentStatus status,
        String paymentMethodCode,
        BigDecimal amount,
        String currency,
        String message
) {
}