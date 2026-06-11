package com.sep.psp.back.feature_payment.event;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;

public record PaymentResultNotificationEvent(
        String paymentId,
        String merchantId,
        String sellerReference,
        String merchantOrderId,
        PaymentStatus status,
        String paymentMethodCode,
        String message
) {
}