package com.sep.psp.back.feature_payment.dto;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after PSP payment transaction is created.")
public record CreatePaymentResponse(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId,

        @Schema(
                description = "Redirect URL to PSP payment page.",
                example = "http://localhost:3000/payment/0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String redirectUrl,

        @Schema(
                description = "Initial PSP payment transaction status.",
                example = "CREATED"
        )
        PaymentStatus status
) {
}