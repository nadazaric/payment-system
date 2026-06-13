package com.sep.bank.back.feature_payment.dto;

import java.time.LocalDateTime;

public record PaymentStatusCheckRequest(
        String bankMerchantId,
        String stan,
        LocalDateTime pspTimestamp
) {
}