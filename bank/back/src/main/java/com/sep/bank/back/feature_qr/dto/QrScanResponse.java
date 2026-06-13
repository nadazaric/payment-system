package com.sep.bank.back.feature_qr.dto;

import java.math.BigDecimal;

public record QrScanResponse(
        String paymentReference,
        String recipientName,
        String recipientAccount,
        BigDecimal amount,
        String currency,
        String paymentPurpose
) {
}