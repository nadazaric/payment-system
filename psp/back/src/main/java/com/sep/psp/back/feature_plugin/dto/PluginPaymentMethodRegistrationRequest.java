package com.sep.psp.back.feature_plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payment method provided by a payment plugin.")
public record PluginPaymentMethodRegistrationRequest(

        @NotBlank(message = "Payment method code is required.")
        @Schema(
                description = "Payment method code.",
                example = "MOCK_PAY"
        )
        String code,

        @NotBlank(message = "Payment method display name is required.")
        @Schema(
                description = "Payment method display name.",
                example = "Mock payment"
        )
        String displayName,

        @Schema(
                description = "Shows whether this payment method is active. If omitted, true is used.",
                example = "true"
        )
        Boolean active,

        @Schema(
                description = "Shows whether existing seller configurations for this method must be invalidated.",
                example = "false"
        )
        Boolean updateRequired,

        @NotBlank(message = "Configuration schema JSON is required.")
        @Schema(
                description = "Configuration schema required by the plugin. PSP stores only field metadata, not configuration values.",
                example = "[{\"fieldKey\":\"mockApiKey\",\"label\":\"Mock API key\",\"fieldType\":\"PASSWORD\",\"required\":true,\"sensitive\":true}]"
        )
        String configSchemaJson
) {
}