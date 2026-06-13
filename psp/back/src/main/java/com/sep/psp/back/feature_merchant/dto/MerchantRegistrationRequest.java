package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request used by a shop admin to register a new merchant shop on PSP.")
public record MerchantRegistrationRequest(

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
        String errorUrl,

        @NotBlank(message = "Admin username is required.")
        @Size(max = 100, message = "Admin username must be at most 100 characters long.")
        @Schema(
                description = "Username used by the merchant admin to log in to the PSP merchant portal.",
                example = "admin"
        )
        String adminUsername,

        @NotBlank(message = "Admin password is required.")
        @Size(min = 8, message = "Admin password must contain at least 8 characters.")
        @Schema(
                description = "Password used by the merchant admin to log in to the PSP merchant portal.",
                example = "admin123"
        )
        String adminPassword,

        @NotBlank(message = "Admin name is required.")
        @Size(max = 120, message = "Admin name must be at most 120 characters long.")
        @Schema(
                description = "Full name of the merchant admin.",
                example = "Vehicle Rental Admin"
        )
        String adminName
) {
}