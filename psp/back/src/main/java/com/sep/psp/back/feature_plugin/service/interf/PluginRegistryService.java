package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_plugin.dto.PluginRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginRegistrationResponse;

public interface PluginRegistryService {

    PluginRegistrationResponse registerPlugin(PluginRegistrationRequest request);

}