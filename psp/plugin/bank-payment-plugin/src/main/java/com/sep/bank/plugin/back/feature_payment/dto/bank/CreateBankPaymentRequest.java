package com.sep.bank.plugin.back.feature_payment.dto.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateBankPaymentRequest(
        String bankMerchantId,
        String stan,
        LocalDateTime pspTimestamp,
        String paymentMethod,
        BigDecimal amount,
        String currency,
        String successUrl,
        String failUrl,
        String errorUrl,
        String pluginCallbackUrl
) {
}