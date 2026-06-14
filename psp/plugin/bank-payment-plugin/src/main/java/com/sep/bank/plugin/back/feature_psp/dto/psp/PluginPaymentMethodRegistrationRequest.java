package com.sep.bank.plugin.back.feature_psp.dto.psp;

public record PluginPaymentMethodRegistrationRequest(
        String code,
        String displayName,
        boolean active,
        boolean updateRequired,
        String configSchemaJson
) {
}
