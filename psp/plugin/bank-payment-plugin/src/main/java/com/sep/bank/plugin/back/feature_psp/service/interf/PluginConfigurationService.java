package com.sep.bank.plugin.back.feature_psp.service.interf;

import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationRequest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationResponse;

public interface PluginConfigurationService {

    PluginConfigurationResponse configure(
            PluginConfigurationRequest request
    );

}
