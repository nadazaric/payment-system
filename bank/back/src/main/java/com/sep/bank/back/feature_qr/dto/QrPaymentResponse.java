package com.sep.bank.back.feature_qr.dto;

public record QrPaymentResponse(
        String status,
        String message
) {
}