package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request used by a merchant admin to log in to the PSP merchant portal.")
public record MerchantLoginRequest(

        @NotBlank(message = "Username is required.")
        @Schema(
                description = "Merchant admin username.",
                example = "admin"
        )
        String username,

        @NotBlank(message = "Password is required.")
        @Schema(
                description = "Merchant admin password.",
                example = "admin123"
        )
        String password
) {
}