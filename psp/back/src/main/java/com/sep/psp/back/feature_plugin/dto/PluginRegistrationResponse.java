package com.sep.psp.back.feature_plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Plugin registration result.")
public record PluginRegistrationResponse(

        @Schema(
                description = "Registered plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "Codes of registered or updated payment methods."
        )
        List<String> registeredMethods,

        @Schema(
                description = "Registration result message.",
                example = "Plugin registered successfully."
        )
        String message
) {
}