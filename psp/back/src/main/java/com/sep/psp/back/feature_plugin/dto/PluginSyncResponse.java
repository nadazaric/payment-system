package com.sep.psp.back.feature_plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Synchronized plugin code.")
public record PluginSyncResponse(

        @Schema(
                description = "Registered plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @Schema(
                description = "Codes of added or updated payment methods."
        )
        List<String> paymentMethods,

        @Schema(
                description = "Plugin sync result message.",
                example = "Plugin synchronized successfully."
        )
        String message
) {
}