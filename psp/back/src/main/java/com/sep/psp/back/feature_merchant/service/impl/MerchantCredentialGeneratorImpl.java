package com.sep.psp.back.feature_merchant.service.impl;

import com.sep.psp.back.feature_merchant.service.interf.MerchantCredentialGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class MerchantCredentialGeneratorImpl implements MerchantCredentialGenerator {

    @Value("${app.merchant-id.prefix}")
    private String merchantIdPrefix;

    @Value("${app.merchant-id.alphabet}")
    private String merchantIdAlphabet;

    @Value("${app.merchant-id.length}")
    private int merchantIdLength;

    @Value("${app.merchant-password.prefix}")
    private String merchantPasswordPrefix;

    @Value("${app.merchant-password.alphabet}")
    private String merchantPasswordAlphabet;

    @Value("${app.merchant-password.length}")
    private int merchantPasswordLength;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateMerchantId() {
        return merchantIdPrefix + generateRandomValue(merchantIdAlphabet, merchantIdLength);
    }

    @Override
    public String generateMerchantPassword() {
        return merchantPasswordPrefix + generateRandomValue(merchantPasswordAlphabet, merchantPasswordLength);
    }

    private String generateRandomValue(String alphabet, int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(alphabet.length());
            builder.append(alphabet.charAt(index));
        }

        return builder.toString();
    }
}