package com.sep.bank.back.feature_qr.dto;

import java.math.BigDecimal;
import java.util.List;

public record QrScanResponse(
        String paymentReference,
        String recipientName,
        String recipientAccount,
        BigDecimal amount,
        String currency,
        String paymentPurpose,
        List<MockMbankingAccountOptionResponse> payerAccounts
) {
}