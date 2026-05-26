package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after regenerating merchant password/API key.")
public record RegenerateMerchantPasswordResponse(

        @Schema(
                description = "New generated merchant password/API key. It is shown only once and must be stored by the merchant.",
                example = "psp_4f8a92d7c3e14b7a9f21xK8mN3pQ5rT"
        )
        String merchantPassword
) {
}