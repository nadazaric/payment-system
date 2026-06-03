package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_plugin.dto.PluginSyncResponse;

public interface PluginSyncService {

    PluginSyncResponse syncPlugin(
            String pluginCodeHeader,
            String timestamp,
            String signature,
            String requestBody
    );

}