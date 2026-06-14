package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for initiating payment with the selected payment method.")
public record InitiatePaymentRequest(

        @Schema(
                description = "Selected payment method code.",
                example = "MOCK_PAY"
        )
        @NotBlank
        String paymentMethodCode
) {
}