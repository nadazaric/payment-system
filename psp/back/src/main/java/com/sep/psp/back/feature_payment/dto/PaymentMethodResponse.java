package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available payment method information.")
public record PaymentMethodResponse(

        @Schema(
                description = "Payment method code.",
                example = "CARD"
        )
        String code,

        @Schema(
                description = "Payment method display name.",
                example = "Payment card"
        )
        String displayName,

        @Schema(
                description = "Shows whether the payment method is globally active in PSP.",
                example = "true"
        )
        boolean active
) {
}