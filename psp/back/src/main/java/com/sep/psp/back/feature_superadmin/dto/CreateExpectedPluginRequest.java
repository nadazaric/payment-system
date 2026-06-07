package com.sep.psp.back.feature_superadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for creating an expected payment plugin.")
public record CreateExpectedPluginRequest(

        @NotBlank(message = "Plugin code is required.")
        @Schema(
                description = "Unique plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @NotBlank(message = "Plugin display name is required.")
        @Schema(
                description = "Plugin display name.",
                example = "Mock Payment Plugin"
        )
        String displayName
) {
}