package com.sep.psp.back.feature_superadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response returned after creating an expected payment plugin.")
public record CreateExpectedPluginResponse(

        @Schema(
                description = "Plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "Plugin display name.",
                example = "Mock Payment Plugin"
        )
        String displayName,

        @Schema(
                description = "Generated plugin secret. It is shown only once."
        )
        String pluginSecret,

        @Schema(
                description = "Shows whether plugin is active.",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Shows whether plugin has already synchronized its manifest.",
                example = "false"
        )
        boolean registered,

        @Schema(
                description = "Result message.",
                example = "Expected plugin created successfully."
        )
        String message
) {
}