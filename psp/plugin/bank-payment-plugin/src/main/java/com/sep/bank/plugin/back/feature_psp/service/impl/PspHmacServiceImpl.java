package com.sep.bank.plugin.back.feature_psp.service.impl;

import com.sep.bank.plugin.back.feature_psp.service.interf.PspHmacService;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Service
public class PspHmacServiceImpl implements PspHmacService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String PAYLOAD_SEPARATOR = ".";

    @Override
    public String generateSignature(
            String secret,
            String timestamp,
            String requestBody
    ) {
        try {
            String payload = timestamp + PAYLOAD_SEPARATOR + requestBody;

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKeySpec);

            byte[] signatureBytes = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signatureBytes);
        } catch (Exception exception) {
            throw new IllegalStateException(LogStrings.Reason.SIGNATURE_GENERATION_FAILED);
        }
    }

    @Override
    public boolean isSignatureValid(
            String secret,
            String timestamp,
            String requestBody,
            String receivedSignature
    ) {
        String expectedSignature = generateSignature(
                secret,
                timestamp,
                requestBody
        );

        return MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                receivedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

}