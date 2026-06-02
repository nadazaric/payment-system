package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Seller payment method configuration result.")
public record ConfigureSellerPaymentMethodResponse(

        @Schema(
                description = "Payment method code.",
                example = "MOCK_PAY"
        )
        String paymentMethodCode,

        @Schema(
                description = "Shows whether the payment method is configured.",
                example = "true"
        )
        Boolean configured,

        @Schema(
                description = "Configuration result message.",
                example = "Payment method configured successfully."
        )
        String message
) {
}