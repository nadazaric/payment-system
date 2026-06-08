package com.sep.psp.back.feature_payment.dto;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;

public record CreatePaymentResponse(
        String paymentId,
        String redirectUrl,
        PaymentStatus status
) {
}