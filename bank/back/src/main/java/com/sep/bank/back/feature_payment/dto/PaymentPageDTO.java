package com.sep.bank.back.feature_payment.dto;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentPageDTO(
        UUID paymentId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String currency
) {
}