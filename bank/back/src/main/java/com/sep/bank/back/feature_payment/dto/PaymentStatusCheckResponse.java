package com.sep.bank.back.feature_payment.dto;

import java.time.LocalDateTime;

public record PaymentStatusCheckResponse(
        String status,
        String message,
        String globalTransactionId,
        LocalDateTime acquirerTimestamp
) {
}