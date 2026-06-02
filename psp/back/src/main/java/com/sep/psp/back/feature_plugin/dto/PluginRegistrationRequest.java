package com.sep.psp.back.feature_plugin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request used by a plugin to synchronize itself and its payment methods on PSP.")
public record PluginRegistrationRequest(

        @NotBlank(message = "Plugin code is required.")
        @Schema(
                description = "Plugin code.",
                example = "MOCK_PLUGIN"
        )
        String pluginCode,

        @NotBlank(message = "Plugin display name is required.")
        @Schema(
                description = "Plugin display name.",
                example = "Mock Payment Plugin"
        )
        String displayName,

        @NotBlank(message = "Plugin base URL is required.")
        @Schema(
                description = "Base URL of the plugin service.",
                example = "http://localhost:8085"
        )
        String baseUrl,

        @Valid
        @NotEmpty(message = "At least one payment method must be provided.")
        @Schema(description = "Payment methods provided by this plugin.")
        List<PluginPaymentMethodRegistrationRequest> methods
) {
}