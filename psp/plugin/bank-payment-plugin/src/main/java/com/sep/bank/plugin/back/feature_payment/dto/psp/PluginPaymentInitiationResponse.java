package com.sep.bank.plugin.back.feature_payment.dto.psp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned by bank payment plugin after payment flow is initiated.")
public record PluginPaymentInitiationResponse(

        @Schema(
                description = "URL where the customer should be redirected to continue payment.",
                example = "http://localhost:8083/payments/5f2f3b2c-0c5f-4c1a-9b28-7fcb8d4a5f90"
        )
        String redirectUrl
) {
}
