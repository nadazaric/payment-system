package com.sep.psp.back.feature_payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Available payment method information.")
public record PaymentMethodResponse(

        @Schema(
                description = "Payment method code.",
                example = "CARD"
        )
        String code,

        @Schema(
                description = "Payment method display name.",
                example = "Payment card"
        )
        String displayName,

        @Schema(
                description = "Shows whether the payment method is globally active in PSP.",
                example = "true"
        )
        Boolean active,

        @Schema(
                description = "Code of the plugin that provides this payment method.",
                example = "BANK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "JSON schema-like configuration metadata required by the plugin. PSP stores only field metadata, not configuration values.",
                example = "[{\"fieldKey\":\"bankMerchantId\",\"label\":\"Bank merchant ID\",\"fieldType\":\"TEXT\",\"required\":true,\"sensitive\":false}]"
        )
        String configSchemaJson
) {
}