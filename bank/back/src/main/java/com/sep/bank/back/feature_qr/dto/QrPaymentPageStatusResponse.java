package com.sep.bank.back.feature_qr.dto;

public record QrPaymentPageStatusResponse(
        String status,
        String message,
        String redirectUrl
) {
}