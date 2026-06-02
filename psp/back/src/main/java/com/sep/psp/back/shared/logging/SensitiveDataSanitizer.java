package com.sep.psp.back.shared.logging;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class SensitiveDataSanitizer {

    private static final String MASK = "****";

    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password",
            "merchantPassword",
            "apiKey",
            "secret",
            "token",
            "authorization",
            "cardNumber",
            "pan",
            "securityCode",
            "cvv",
            "cvc"
    );

    private static final Pattern AUTHORIZATION_BEARER_PATTERN = Pattern.compile(
            "(?i)(Authorization\\s*[:=]\\s*Bearer\\s+)([^,\\s]+)"
    );

    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile(
            "(?i)(password|merchantPassword|apiKey|secret|token|authorization|cardNumber|pan|securityCode|cvv|cvc)\\s*[:=]\\s*([^,}\\s]+)"
    );

    private SensitiveDataSanitizer() {
    }

    public static String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String sanitized = AUTHORIZATION_BEARER_PATTERN
                .matcher(value)
                .replaceAll("$1" + MASK);

        return KEY_VALUE_PATTERN
                .matcher(sanitized)
                .replaceAll("$1=" + MASK);
    }

    public static Map<String, Object> sanitize(Map<String, Object> values) {
        if (values == null || values.isEmpty()) {
            return Map.of();
        }

        return values.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sanitizeValue(entry.getKey(), entry.getValue())
                ));
    }

    private static Object sanitizeValue(
            String key,
            Object value
    ) {
        if (key == null) {
            return value;
        }

        if (isSensitiveKey(key)) {
            return MASK;
        }

        if (value instanceof String stringValue) {
            return sanitize(stringValue);
        }

        return value;
    }

    private static boolean isSensitiveKey(String key) {
        return SENSITIVE_KEYS
                .stream()
                .anyMatch(sensitiveKey -> sensitiveKey.equalsIgnoreCase(key));
    }
}
