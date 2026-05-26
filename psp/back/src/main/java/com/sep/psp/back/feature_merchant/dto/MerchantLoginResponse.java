package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after successful merchant admin login.")
public record MerchantLoginResponse(

        @Schema(
                description = "JWT token used for authenticated merchant admin requests.",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String token
) {
}