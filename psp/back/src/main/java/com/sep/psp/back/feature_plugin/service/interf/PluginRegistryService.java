package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_plugin.dto.PluginRegistrationResponse;

public interface PluginRegistryService {

    PluginRegistrationResponse syncPlugin(
            String pluginCodeHeader,
            String timestamp,
            String signature,
            String requestBody
    );

}