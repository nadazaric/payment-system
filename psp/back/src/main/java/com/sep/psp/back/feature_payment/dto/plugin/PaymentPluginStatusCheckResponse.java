package com.sep.psp.back.feature_payment.dto.plugin;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned by payment plugin after PSP payment status check.")
public record PaymentPluginStatusCheckResponse(

        @Schema(
                description = "Current payment status known by payment plugin.",
                example = "SUCCESS"
        )
        PaymentStatus status,

        @Schema(
                description = "Optional status message.",
                example = "Payment completed successfully.",
                nullable = true
        )
        String message
) {
}