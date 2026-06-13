package com.sep.bank.back.feature_payment.dto;

import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;

public record BankPaymentCallbackResponse(
        String bankPaymentId,
        PaymentStatus status,
        String resultDeliveryStatus
) {
}