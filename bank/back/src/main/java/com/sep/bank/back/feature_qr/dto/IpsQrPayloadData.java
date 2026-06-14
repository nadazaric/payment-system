package com.sep.bank.back.feature_qr.dto;

import java.math.BigDecimal;

public record IpsQrPayloadData(
        String identificationCode,
        String version,
        String characterSet,
        String recipientAccount,
        String recipientName,
        BigDecimal amount,
        String currency,
        String paymentCode,
        String paymentPurpose,
        String paymentReference
) {
}