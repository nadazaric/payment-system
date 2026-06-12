package com.sep.bank.plugin.back.feature_psp.dto.psp;

import java.util.List;

public record PluginSyncResponse(
        String pluginCode,
        List<String> paymentMethods,
        String message
) {
}
