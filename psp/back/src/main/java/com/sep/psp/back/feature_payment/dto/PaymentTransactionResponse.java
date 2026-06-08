package com.sep.psp.back.feature_payment.dto;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public record PaymentTransactionResponse(
        String paymentId,
        String merchantName,
        String sellerReference,
        String sellerDisplayName,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        List<PaymentOptionResponse> paymentMethods
) {
}