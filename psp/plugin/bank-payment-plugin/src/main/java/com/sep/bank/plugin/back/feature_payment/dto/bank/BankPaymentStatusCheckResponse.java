package com.sep.bank.plugin.back.feature_payment.dto.bank;

import java.time.LocalDateTime;

public record BankPaymentStatusCheckResponse(
        String status,
        String message,
        String globalTransactionId,
        LocalDateTime acquirerTimestamp
) {
}