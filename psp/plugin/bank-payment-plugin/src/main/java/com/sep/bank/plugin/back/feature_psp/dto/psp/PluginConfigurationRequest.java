package com.sep.bank.plugin.back.feature_psp.dto.psp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

@Schema(description = "Configuration request sent from PSP to bank payment plugin.")
public record PluginConfigurationRequest(

        @Schema(
                description = "PSP merchant ID.",
                example = "MER-TEST0001"
        )
        @NotBlank
        String merchantId,

        @Schema(
                description = "Seller reference inside merchant.",
                example = "MAIN_SELLER"
        )
        @NotBlank
        String sellerReference,

        @Schema(
                description = "Payment method code.",
                example = "CARD"
        )
        @NotBlank
        String paymentMethodCode,

        @Schema(
                description = "Configuration values.",
                example = "{\"bankMerchantId\":\"BANK_MERCHANT_001\"}"
        )
        @NotEmpty
        Map<String, String> values
) {
}