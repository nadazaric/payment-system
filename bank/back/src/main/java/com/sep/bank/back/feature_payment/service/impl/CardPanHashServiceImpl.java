package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.service.interf.CardPanHashService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CardPanHashServiceImpl implements CardPanHashService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    @Value("${app.security.pan-hash-secret}")
    String panHashSecret;

    @Override
    public String generatePanHash(String normalizedPan) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    panHashSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(
                    normalizedPan.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);
        } catch (Exception exception) {
            throw new IllegalStateException("PAN hash could not be generated.");
        }
    }

}