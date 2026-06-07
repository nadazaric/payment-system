package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment method configuration field.")
public record PaymentMethodConfigField(

        @Schema(
                description = "Configuration field name.",
                example = "mockApiKey"
        )
        String fieldName,

        @Schema(
                description = "Configuration field type.",
                example = "PASSWORD"
        )
        String fieldType
) {
}