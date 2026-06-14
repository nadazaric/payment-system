package com.sep.bank.plugin.back.feature_payment.dto.bank;

public record CreateBankPaymentResponse(
        String bankPaymentId,
        String paymentUrl
) {
}