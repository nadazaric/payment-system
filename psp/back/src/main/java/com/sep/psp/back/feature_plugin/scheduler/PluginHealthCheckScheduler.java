package com.sep.psp.back.feature_plugin.scheduler;

import com.sep.psp.back.feature_plugin.service.interf.PluginHealthCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PluginHealthCheckScheduler {

    @Autowired
    PluginHealthCheckService pluginHealthCheckService;

    @Scheduled(fixedDelayString = "${app.plugin.health-check-rate-ms:30000}")
    public void checkPlugins() {
        pluginHealthCheckService.checkPlugins();
    }

}