package com.sep.bank.back.feature_qr.service.interf;

import java.math.BigDecimal;

public interface IpsQrPayloadService {

    String buildPayload(
            String recipientAccount,
            String recipientName,
            BigDecimal amount,
            String currency,
            String paymentCode,
            String paymentPurpose,
            String paymentReference
    );

}