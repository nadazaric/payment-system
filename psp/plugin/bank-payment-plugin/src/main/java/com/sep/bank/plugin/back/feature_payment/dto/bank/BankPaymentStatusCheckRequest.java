package com.sep.bank.plugin.back.feature_payment.dto.bank;

import java.time.LocalDateTime;

public record BankPaymentStatusCheckRequest(
        String bankMerchantId,
        String stan,
        LocalDateTime pspTimestamp
) {
}