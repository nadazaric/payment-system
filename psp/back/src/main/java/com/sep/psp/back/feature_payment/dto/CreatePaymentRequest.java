package com.sep.psp.back.feature_payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreatePaymentRequest(
        @NotBlank
        String merchantId,

        @NotBlank
        String merchantPassword,

        @NotBlank
        String sellerReference,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount,

        @NotBlank
        String currency,

        @NotBlank
        String merchantOrderId,

        @NotNull
        LocalDateTime merchantTimestamp
) {
}