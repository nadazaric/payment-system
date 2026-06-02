package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Merchant seller account information.")
public record MerchantSellerAccountResponse(

        @Schema(
                description = "Seller account ID.",
                example = "seller-test-001"
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
                description = "Shows whether this seller account has at least one currently available payment method.",
                example = "true"
        )
        Boolean active,

        @Schema(description = "Payment methods configured or requiring configuration for this seller account.")
        List<SellerPaymentMethodResponse> paymentMethods
) {
}