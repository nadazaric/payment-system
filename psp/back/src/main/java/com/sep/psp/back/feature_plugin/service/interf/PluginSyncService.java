package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_plugin.dto.PluginSyncRequest;
import com.sep.psp.back.feature_plugin.dto.PluginSyncResponse;

public interface PluginSyncService {

    PluginSyncResponse syncPlugin(PluginSyncRequest request);

}