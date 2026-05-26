package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Merchant seller account information.")
public record MerchantSellerAccountResponse(

        @Schema(
                description = "Seller account ID.",
                example = "7f3e7a84-2f4d-4f9b-8e4a-ccf3bcd24a11"
        )
        String id,

        @Schema(
                description = "Seller reference used by the merchant system.",
                example = "MAIN_SELLER"
        )
        String sellerReference,

        @Schema(
                description = "Seller display name.",
                example = "Main seller"
        )
        String displayName,

        @Schema(
                description = "Shows whether this seller account is active for payments.",
                example = "false"
        )
        boolean active
) {
}