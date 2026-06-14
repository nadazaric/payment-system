package com.sep.bank.back.feature_qr.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MockMbankingAccountOptionResponse(
        UUID accountId,
        String accountNumber,
        String holderName,
        BigDecimal balance,
        String currency
) {
}