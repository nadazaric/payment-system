package com.sep.bank.plugin.back.feature_psp.dto.psp;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Configuration response returned by bank payment plugin.")
public record PluginConfigurationResponse(

        @Schema(
                description = "Shows whether configuration was accepted by plugin.",
                example = "true"
        )
        boolean configured,

        @Schema(
                description = "Configuration result message.",
                example = "Configuration saved successfully."
        )
        String message
) {
}