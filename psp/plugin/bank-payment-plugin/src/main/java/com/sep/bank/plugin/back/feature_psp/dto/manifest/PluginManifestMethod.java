package com.sep.bank.plugin.back.feature_psp.dto.manifest;

import java.util.List;

public record PluginManifestMethod(
        String code,
        String displayName,
        boolean active,
        boolean updateRequired,
        List<PluginConfigField> configFields
) {
}
