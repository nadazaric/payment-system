package com.sep.psp.back.feature_payment.dto;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after selected payment method is initiated.")
public record InitiatePaymentResponse(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId,

        @Schema(
                description = "Selected payment method code.",
                example = "MOCK_PAY"
        )
        String selectedPaymentMethodCode,

        @Schema(
                description = "Updated PSP payment transaction status.",
                example = "INITIATED"
        )
        PaymentStatus status,

        @Schema(
                description = "Redirect URL returned by payment plugin. Currently null until plugin payment initiation is implemented.",
                example = "http://localhost:8085/payment/123",
                nullable = true
        )
        String redirectUrl
) {
}