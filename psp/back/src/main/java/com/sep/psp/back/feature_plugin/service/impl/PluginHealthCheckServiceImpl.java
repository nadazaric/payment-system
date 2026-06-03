package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginAvailabilityService;
import com.sep.psp.back.feature_plugin.service.interf.PluginHealthCheckService;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PluginHealthCheckServiceImpl implements PluginHealthCheckService {

    final String heartbeatEndpoint = "/api/plugin/heartbeat";

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PluginHttpClient pluginHttpClient;

    @Autowired
    PluginAvailabilityService pluginAvailabilityService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public void checkPlugins() {
        List<PaymentPlugin> plugins = paymentPluginRepository.findByActiveByAdminTrueAndBaseUrlIsNotNull();

        plugins.forEach(this::checkPlugin);
    }

    private void checkPlugin(PaymentPlugin paymentPlugin) {
        boolean wasActive = paymentPlugin.isActive();

        try {
            pluginHttpClient.postWithoutResponse(
                    paymentPlugin,
                    heartbeatEndpoint,
                    Map.of(),
                    false
            );

            pluginAvailabilityService.markPluginActive(paymentPlugin);
        } catch (Exception exception) {
            pluginAvailabilityService.markPluginInactive(paymentPlugin);

            if (wasActive) {
                appLoggerService.warn(
                        LogStrings.Feature.PAYMENT_PLUGIN,
                        LogStrings.Action.PLUGIN_HEALTH_CHECK_FAILED,
                        "pluginCode={} error={}",
                        paymentPlugin.getCode(),
                        exception.getMessage()
                );
            }
        }
    }

}