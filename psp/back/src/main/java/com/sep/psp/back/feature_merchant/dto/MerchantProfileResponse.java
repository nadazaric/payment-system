package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Profile information for the currently authenticated merchant admin.")
public record MerchantProfileResponse(

        @Schema(
                description = "Merchant ID used by the web shop when communicating with PSP.",
                example = "MER-8F4K2Q9L"
        )
        String merchantId,

        @Schema(
                description = "Registered merchant shop name.",
                example = "Vehicle Rental Agency"
        )
        String merchantName,

        @Schema(
                description = "Default merchant currency.",
                example = "RSD"
        )
        String currency,

        @Schema(
                description = "URL where the customer is redirected after successful payment.",
                example = "http://localhost:3000/payment/success"
        )
        String successUrl,

        @Schema(
                description = "URL where the customer is redirected after failed payment.",
                example = "http://localhost:3000/payment/failed"
        )
        String failUrl,

        @Schema(
                description = "URL where the customer is redirected if payment error occurs.",
                example = "http://localhost:3000/payment/error"
        )
        String errorUrl,

        @Schema(
                description = "Shows whether the merchant is active for payment flow.",
                example = "false"
        )
        boolean merchantActive,

        @Schema(
                description = "Username of the currently authenticated merchant admin.",
                example = "admin@vehicle-rental.com"
        )
        String adminUsername,

        @Schema(
                description = "Name of the currently authenticated merchant admin.",
                example = "Vehicle Rental Admin"
        )
        String adminName
) {
}