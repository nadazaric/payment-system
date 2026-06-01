package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment method status for one seller account.")
public record SellerPaymentMethodResponse(

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
                description = "Code of the plugin that provides this payment method.",
                example = "BANK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "Shows whether this payment method requires configuration before it can be used.",
                example = "false"
        )
        boolean configurationRequired
) {
}