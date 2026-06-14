package com.sep.psp.back.feature_payment.event;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResultNotificationEvent(
        String paymentId,
        String merchantId,
        String sellerReference,
        String merchantOrderId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String paymentMethodCode,
        String message
) {
}