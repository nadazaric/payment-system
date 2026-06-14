package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after successful merchant shop registration.")
public record MerchantRegistrationResponse(

        @Schema(
                description = "Generated merchant ID used by the web shop when communicating with PSP.",
                example = "MER-8F4K2Q9L"
        )
        String merchantId,

        @Schema(
                description = "Generated merchant password/API key. It is shown only once and must be stored by the merchant.",
                example = "psp_4f8a92d7c3e14b7a9f21"
        )
        String merchantPassword,

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
                description = "Username of the created merchant admin account.",
                example = "admin"
        )
        String adminUsername,

        @Schema(
                description = "ID of the automatically created default seller account.",
                example = "a1b2c3d4-1111-2222-3333-abcdef123456"
        )
        String defaultSellerId,

        @Schema(
                description = "Reference of the automatically created default seller account.",
                example = "MAIN_SELLER"
        )
        String defaultSellerReference

) {
}