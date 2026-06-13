package com.sep.bank.back.feature_qr.dto;

import jakarta.validation.constraints.NotBlank;

public record QrScanRequest(
        @NotBlank
        String payload
) {
}