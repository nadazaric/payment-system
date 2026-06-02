package com.sep.psp.back.feature_superadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment plugin information for super admin.")
public record SuperAdminPluginResponse(

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
                description = "Plugin base URL.",
                example = "http://localhost:8085"
        )
        String baseUrl,

        @Schema(
                description = "Shows whether plugin is active.",
                example = "true"
        )
        boolean active,

        @Schema(
                description = "Shows whether plugin has synchronized its manifest.",
                example = "true"
        )
        boolean registered
) {
}