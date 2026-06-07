package com.sep.psp.back.feature_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request.")
public record LoginRequest(

        @NotBlank(message = "Username is required.")
        @Schema(
                description = "Username.",
                example = "admin"
        )
        String username,

        @NotBlank(message = "Password is required.")
        @Schema(
                description = "Password.",
                example = "admin"
        )
        String password
) {
}