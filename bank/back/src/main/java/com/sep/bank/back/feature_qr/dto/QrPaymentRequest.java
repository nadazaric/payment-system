package com.sep.bank.back.feature_qr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record QrPaymentRequest(
        @NotBlank
        String payload,

        @NotNull
        UUID payerAccountId
) {
}