package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request used by a merchant admin to create a new seller account.")
public record CreateMerchantSellerAccountRequest(

        @NotBlank(message = "Seller reference is required.")
        @Size(max = 80, message = "Seller reference must be at most 80 characters long.")
        @Schema(
                description = "Business reference of the seller account, unique within one merchant.",
                example = "SECOND_SELLER"
        )
        String sellerReference,

        @NotBlank(message = "Display name is required.")
        @Size(max = 120, message = "Display name must be at most 120 characters long.")
        @Schema(
                description = "Display name of the seller account.",
                example = "Second Seller"
        )
        String displayName
) {
}