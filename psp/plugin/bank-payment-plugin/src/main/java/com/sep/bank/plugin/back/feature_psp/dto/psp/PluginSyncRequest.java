package com.sep.bank.plugin.back.feature_psp.dto.psp;

import java.util.List;

public record PluginSyncRequest(
        String pluginCode,
        String displayName,
        String baseUrl,
        List<PluginPaymentMethodRegistrationRequest> methods
) {
}
