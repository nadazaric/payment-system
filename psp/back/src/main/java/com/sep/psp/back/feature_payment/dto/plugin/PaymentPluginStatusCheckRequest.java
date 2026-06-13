package com.sep.psp.back.feature_payment.dto.plugin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request sent by PSP to payment plugin when checking payment status.")
public record PaymentPluginStatusCheckRequest(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId
) {
}