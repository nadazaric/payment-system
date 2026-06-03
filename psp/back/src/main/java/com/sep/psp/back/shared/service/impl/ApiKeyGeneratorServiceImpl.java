package com.sep.psp.back.shared.service.impl;

import com.sep.psp.back.shared.service.interf.ApiKeyGeneratorService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ApiKeyGeneratorServiceImpl implements ApiKeyGeneratorService {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateApiKey(
            String prefix,
            String alphabet,
            int length
    ) {
        validateGeneratorParameters(
                alphabet,
                length
        );

        return normalizePrefix(prefix) + generateRandomValue(
                alphabet,
                length
        );
    }

    private void validateGeneratorParameters(
            String alphabet,
            int length
    ) {
        if (alphabet == null || alphabet.isBlank()) {
            throw new IllegalArgumentException("API key alphabet must not be empty.");
        }

        if (length <= 0) {
            throw new IllegalArgumentException("API key length must be greater than zero.");
        }
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null) {
            return "";
        }

        return prefix;
    }

    private String generateRandomValue(
            String alphabet,
            int length
    ) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(alphabet.length());
            builder.append(alphabet.charAt(index));
        }

        return builder.toString();
    }
}