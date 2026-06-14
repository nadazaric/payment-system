package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_qr.service.interf.IpsQrPayloadService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class IpsQrPayloadServiceImpl implements IpsQrPayloadService {

    private static final String IDENTIFICATION_CODE = "PR";
    private static final String VERSION = "01";
    private static final String CHARACTER_SET = "1";
    private static final int AMOUNT_SCALE = 2;

    @Override
    public String buildPayload(
            String recipientAccount,
            String recipientName,
            BigDecimal amount,
            String currency,
            String paymentCode,
            String paymentPurpose,
            String paymentReference
    ) {
        return String.join(
                "|",
                "K:" + IDENTIFICATION_CODE,
                "V:" + VERSION,
                "C:" + CHARACTER_SET,
                "R:" + normalizeRequiredValue(recipientAccount, "recipient account", true, false),
                "N:" + normalizeRequiredValue(recipientName, "recipient name", false, false),
                "I:" + formatCurrencyAndAmount(currency, amount),
                "SF:" + normalizeRequiredValue(paymentCode, "payment code", false, false),
                "S:" + normalizeRequiredValue(paymentPurpose, "payment purpose", false, false),
                "RO:" + normalizeRequiredValue(paymentReference, "payment reference", false, false)
        );
    }

    private String formatCurrencyAndAmount(String currency, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("QR amount is required.");
        }

        String normalizedCurrency = normalizeRequiredValue(
                currency,
                "currency",
                false,
                true
        );

        String normalizedAmount = amount
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP)
                .toPlainString()
                .replace('.', ',');

        return normalizedCurrency + normalizedAmount;
    }

    private String normalizeRequiredValue(
            String value,
            String fieldName,
            boolean digitsOnly,
            boolean uppercase
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("QR " + fieldName + " is required.");
        }

        String normalized = value.trim();

        if (digitsOnly) {
            normalized = normalized.replaceAll("\\D", "");
        }

        if (uppercase) {
            normalized = normalized.toUpperCase();
        }

        if (normalized.isBlank()) {
            throw new IllegalArgumentException("QR " + fieldName + " is required.");
        }

        if (normalized.contains("|")) {
            throw new IllegalArgumentException("QR " + fieldName + " cannot contain pipe character.");
        }

        return normalized;
    }

}