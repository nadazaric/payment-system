package com.sep.bank.plugin.back.feature_psp.dto.manifest;

import java.util.List;

public record PluginManifest(
        String pluginCode,
        String displayName,
        List<PluginManifestMethod> methods
) {
}