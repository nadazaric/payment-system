package com.sep.bank.back.feature_payment.dto;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentPageDTO(
        UUID paymentId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        LocalDateTime expiresAt,
        Boolean paymentAttemptUsed,
        Boolean expired
) {
}