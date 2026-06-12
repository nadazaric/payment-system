package com.sep.bank.back.feature_payment.dto;

public record CardPaymentSubmitRequest(
        String pan,
        String securityCode,
        String cardHolderName,
        String expirationDate
) {
}