package com.sep.psp.back.feature_plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Configuration request sent from PSP to payment plugin.")
public record PluginConfigurationRequest(

        @Schema(
                description = "Merchant ID.",
                example = "MER-TEST0001"
        )
        String merchantId,

        @Schema(
                description = "Seller reference.",
                example = "MAIN_SELLER"
        )
        String sellerReference,

        @Schema(
                description = "Payment method code.",
                example = "MOCK_PAY"
        )
        String paymentMethodCode,

        @Schema(description = "Configuration values.")
        Map<String, String> values
) {
}