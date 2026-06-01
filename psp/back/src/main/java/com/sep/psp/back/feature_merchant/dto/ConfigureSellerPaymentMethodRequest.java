package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

@Schema(description = "Request for configuring a seller payment method.")
public record ConfigureSellerPaymentMethodRequest(

        @NotEmpty(message = "Configuration values are required.")
        @Schema(
                description = "Configuration values required by the payment plugin.",
                example = "{\"mockApiKey\":\"test-key\"}"
        )
        Map<String, String> values
) {
}