package com.sep.psp.back.feature_payment.dto.plugin;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request sent by payment plugin to PSP after payment processing is completed.")
public record PaymentPluginCallbackRequest(

        @Schema(
                description = "Final payment status reported by the payment plugin.",
                example = "SUCCESS"
        )
        @NotNull
        PaymentStatus status,

        @Schema(
                description = "Optional message returned by the payment plugin.",
                example = "Mock payment completed successfully.",
                nullable = true
        )
        String message
) {
}