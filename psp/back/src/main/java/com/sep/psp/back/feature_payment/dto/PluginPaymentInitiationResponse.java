package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned by payment plugin after payment flow is initiated.")
public record PluginPaymentInitiationResponse(

        @Schema(
                description = "URL where the customer should be redirected to continue payment.",
                example = "http://localhost:8085/mock-payment?paymentId=0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String redirectUrl
) {
}