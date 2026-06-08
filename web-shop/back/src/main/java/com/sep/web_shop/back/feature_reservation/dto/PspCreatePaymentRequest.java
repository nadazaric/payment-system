package com.sep.web_shop.back.feature_reservation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PspCreatePaymentRequest(
        String merchantId,
        String merchantPassword,
        String sellerReference,
        BigDecimal amount,
        String currency,
        String merchantOrderId,
        LocalDateTime merchantTimestamp
) {
}