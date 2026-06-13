package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_qr.dto.IpsQrPayloadData;
import com.sep.bank.back.feature_qr.service.interf.IpsQrValidatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class IpsQrValidatorServiceImpl implements IpsQrValidatorService {

    private static final String EXPECTED_IDENTIFICATION_CODE = "PR";
    private static final String EXPECTED_VERSION = "01";
    private static final String EXPECTED_CHARACTER_SET = "1";
    private static final String SUPPORTED_CURRENCY = "RSD";

    @Override
    public IpsQrPayloadData validateAndParse(String payload) {
        Map<String, String> fields = parsePayload(payload);

        String identificationCode = getRequiredField(fields, "K");
        String version = getRequiredField(fields, "V");
        String characterSet = getRequiredField(fields, "C");
        String recipientAccount = getRequiredField(fields, "R");
        String recipientName = getRequiredField(fields, "N");
        String currencyAndAmount = getRequiredField(fields, "I");
        String paymentCode = getRequiredField(fields, "SF");
        String paymentPurpose = getRequiredField(fields, "S");
        String paymentReference = getRequiredField(fields, "RO");

        validateExactValue("K", identificationCode, EXPECTED_IDENTIFICATION_CODE);
        validateExactValue("V", version, EXPECTED_VERSION);
        validateExactValue("C", characterSet, EXPECTED_CHARACTER_SET);
        validateRecipientAccount(recipientAccount);
        validatePaymentCode(paymentCode);
        validatePaymentReference(paymentReference);

        ParsedAmount parsedAmount = parseCurrencyAndAmount(currencyAndAmount);

        return new IpsQrPayloadData(
                identificationCode,
                version,
                characterSet,
                recipientAccount,
                recipientName,
                parsedAmount.amount(),
                parsedAmount.currency(),
                paymentCode,
                paymentPurpose,
                paymentReference
        );
    }

    private Map<String, String> parsePayload(String payload) {
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("IPS QR payload is empty.");
        }

        if (payload.startsWith("|") || payload.endsWith("|")) {
            throw new IllegalArgumentException("IPS QR payload has invalid delimiter placement.");
        }

        Map<String, String> fields = new HashMap<>();
        String[] parts = payload.split("\\|");

        for (String part : parts) {
            int separatorIndex = part.indexOf(':');

            if (separatorIndex <= 0 || separatorIndex == part.length() - 1) {
                throw new IllegalArgumentException("IPS QR payload field is not valid.");
            }

            String key = part.substring(0, separatorIndex);
            String value = part.substring(separatorIndex + 1);

            if (fields.containsKey(key)) {
                throw new IllegalArgumentException("IPS QR payload contains duplicate field: " + key);
            }

            fields.put(key, value);
        }

        return fields;
    }

    private String getRequiredField(Map<String, String> fields, String key) {
        String value = fields.get(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IPS QR field " + key + " is required.");
        }

        return value.trim();
    }

    private void validateExactValue(
            String key,
            String actualValue,
            String expectedValue
    ) {
        if (!expectedValue.equals(actualValue)) {
            throw new IllegalArgumentException("IPS QR field " + key + " is not valid.");
        }
    }

    private void validateRecipientAccount(String recipientAccount) {
        if (!recipientAccount.matches("\\d{18}")) {
            throw new IllegalArgumentException("IPS QR recipient account must contain exactly 18 digits.");
        }
    }

    private ParsedAmount parseCurrencyAndAmount(String currencyAndAmount) {
        if (!currencyAndAmount.startsWith(SUPPORTED_CURRENCY)) {
            throw new IllegalArgumentException("IPS QR amount must start with RSD currency.");
        }

        String amountText = currencyAndAmount.substring(SUPPORTED_CURRENCY.length());

        if (!amountText.matches("\\d+,\\d{2}")) {
            throw new IllegalArgumentException("IPS QR amount format is not valid.");
        }

        BigDecimal amount = new BigDecimal(amountText.replace(',', '.'));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("IPS QR amount must be greater than zero.");
        }

        return new ParsedAmount(SUPPORTED_CURRENCY, amount);
    }

    private void validatePaymentCode(String paymentCode) {
        if (!paymentCode.matches("\\d{3}")) {
            throw new IllegalArgumentException("IPS QR payment code must contain exactly 3 digits.");
        }
    }

    private void validatePaymentReference(String paymentReference) {
        if (!paymentReference.matches("\\d{4,24}")) {
            throw new IllegalArgumentException("IPS QR payment reference must contain between 4 and 24 digits.");
        }
    }

    private record ParsedAmount(
            String currency,
            BigDecimal amount
    ) {
    }

}