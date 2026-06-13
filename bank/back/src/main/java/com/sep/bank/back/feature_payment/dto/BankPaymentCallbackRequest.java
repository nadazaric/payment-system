package com.sep.bank.back.feature_payment.dto;

import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;

import java.time.LocalDateTime;

public record BankPaymentCallbackRequest(
        String bankPaymentId,
        String stan,
        PaymentStatus status,
        String globalTransactionId,
        LocalDateTime acquirerTimestamp,
        String message
) {
}