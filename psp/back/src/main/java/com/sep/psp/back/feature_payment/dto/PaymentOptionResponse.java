package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment method option available for payment selection.")
public record PaymentOptionResponse(

        @Schema(
                description = "Payment method code.",
                example = "CARD_PAYMENT"
        )
        String code,

        @Schema(
                description = "Payment method display name.",
                example = "Payment card"
        )
        String displayName
) {
}