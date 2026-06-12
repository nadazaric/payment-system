package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.model.PaymentCard;
import com.sep.bank.back.feature_payment.service.interf.CardSecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class CardSecurityServiceImpl implements CardSecurityService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String PAYLOAD_SEPARATOR = "|";

    @Value("${app.security.card-secret}")
    String cardSecret;

    @Override
    public boolean isSecurityCodeValid(PaymentCard paymentCard, String securityCode) {
        if (securityCode == null || !securityCode.matches("\\d{3}")) {
            return false;
        }

        String expectedSecurityCode = generateSecurityCode(paymentCard);

        return expectedSecurityCode.equals(securityCode);
    }

    private String generateSecurityCode(PaymentCard paymentCard) {
        try {
            String payload = paymentCard.getPan()
                    + PAYLOAD_SEPARATOR
                    + paymentCard.getExpirationMonth()
                    + PAYLOAD_SEPARATOR
                    + paymentCard.getExpirationYear();

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    cardSecret.getBytes(StandardCharsets.UTF_8),
                    HMAC_ALGORITHM
            );

            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            int numericValue = Math.abs(ByteBuffer.wrap(hash).getInt());

            return String.format("%03d",numericValue % 1000);
        } catch (Exception exception) {
            throw new IllegalStateException("Security code could not be generated.");
        }
    }

}