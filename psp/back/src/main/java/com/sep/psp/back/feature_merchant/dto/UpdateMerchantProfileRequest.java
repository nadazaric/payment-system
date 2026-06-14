package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request used by a merchant admin to update merchant profile information.")
public record UpdateMerchantProfileRequest(

        @NotBlank(message = "Merchant name is required.")
        @Size(max = 120, message = "Merchant name must be at most 120 characters long.")
        @Schema(
                description = "Display name of the merchant shop.",
                example = "Vehicle Rental Agency"
        )
        String merchantName,

        @NotBlank(message = "Currency is required.")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code.")
        @Schema(
                description = "Default currency used by the merchant shop.",
                example = "RSD"
        )
        String currency,

        @NotBlank(message = "Success URL is required.")
        @Schema(
                description = "URL where the customer will be redirected after successful payment.",
                example = "http://localhost:3000/payment/success"
        )
        String successUrl,

        @NotBlank(message = "Fail URL is required.")
        @Schema(
                description = "URL where the customer will be redirected after failed payment.",
                example = "http://localhost:3000/payment/failed"
        )
        String failUrl,

        @NotBlank(message = "Error URL is required.")
        @Schema(
                description = "URL where the customer will be redirected if an error occurs during payment.",
                example = "http://localhost:3000/payment/error"
        )
        String errorUrl

) {
}