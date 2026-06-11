package com.sep.psp.back.feature_payment.dto.plugin;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned by PSP after plugin payment result is processed.")
public record PaymentPluginCallbackResponse(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId,

        @Schema(
                description = "Current PSP payment transaction status.",
                example = "SUCCESS"
        )
        PaymentStatus status
) {
}