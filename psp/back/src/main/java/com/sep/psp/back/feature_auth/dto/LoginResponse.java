package com.sep.psp.back.feature_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login response.")
public record LoginResponse(

        @Schema(description = "JWT access token.")
        String token,

        @Schema(
                description = "Authenticated user role.",
                example = "MERCHANT_ADMIN"
        )
        String role
) {
}