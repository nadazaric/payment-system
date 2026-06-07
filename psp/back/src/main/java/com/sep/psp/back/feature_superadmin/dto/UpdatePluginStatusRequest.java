package com.sep.psp.back.feature_superadmin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request for updating payment plugin admin status.")
public record UpdatePluginStatusRequest(

        @NotBlank(message = "Plugin code is required.")
        @Schema(
                description = "Payment plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "Shows whether plugin is allowed by PSP super admin.",
                example = "false"
        )
        boolean activeByAdmin
) {
}