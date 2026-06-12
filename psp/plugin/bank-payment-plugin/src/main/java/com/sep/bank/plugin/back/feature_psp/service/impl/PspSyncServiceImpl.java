package com.sep.bank.plugin.back.feature_psp.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_psp.client.PspClient;
import com.sep.bank.plugin.back.feature_psp.dto.manifest.PluginManifest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginPaymentMethodRegistrationRequest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginSyncRequest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginSyncResponse;
import com.sep.bank.plugin.back.feature_psp.service.interf.PspSyncService;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import com.sep.bank.plugin.back.shared.security.service.interf.HmacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PspSyncServiceImpl implements PspSyncService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ResourceLoader resourceLoader;

    @Autowired
    HmacService hmacService;

    @Autowired
    PspClient pspClient;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.plugin.base-url}")
    String pluginBaseUrl;

    @Value("${app.security.psp-secret}")
    String pspSecret;

    @Value("${app.plugin.manifest-path}")
    String manifestPath;

    @Value("${app.psp.sync.max-attempts:5}")
    int maxSyncAttempts;

    @Value("${app.psp.sync.retry-delay-ms:3000}")
    long syncRetryDelayMs;

    public PspSyncServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void syncWithPsp() {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxSyncAttempts; attempt++) {
            try {
                syncWithPspOnce();

                return;
            } catch (Exception exception) {
                lastException = exception;

                if (attempt == maxSyncAttempts) {
                    break;
                }

                appLoggerService.warn(
                        LogStrings.Feature.PLUGIN_SYNC,
                        LogStrings.Action.PSP_SYNC_RETRY,
                        "reason={} attempt={} maxAttempts={} retryDelayMs={} error={}",
                        LogStrings.Reason.PSP_SYNC_FAILED,
                        attempt,
                        maxSyncAttempts,
                        syncRetryDelayMs,
                        exception.getMessage()
                );

                sleepBeforeRetry();
            }
        }

        appLoggerService.error(
                LogStrings.Feature.PLUGIN_SYNC,
                LogStrings.Action.PSP_SYNC_FAILED,
                "reason={} attempts={} error={}",
                LogStrings.Reason.PSP_SYNC_FAILED,
                maxSyncAttempts,
                lastException == null ? "unknown" : lastException.getMessage()
        );

        throw new IllegalStateException("Bank payment plugin sync with PSP failed.");
    }

    private void syncWithPspOnce() {
        PluginManifest manifest = loadManifest();

        PluginSyncRequest request = new PluginSyncRequest(
                manifest.pluginCode(),
                manifest.displayName(),
                pluginBaseUrl,
                manifest.methods()
                        .stream()
                        .map(method -> new PluginPaymentMethodRegistrationRequest(
                                method.code(),
                                method.displayName(),
                                method.active(),
                                method.updateRequired(),
                                writeJson(method.configFields())
                        ))
                        .toList()
        );

        String requestBody = writeJson(request);
        String timestamp = Instant.now().toString();

        String signature = hmacService.generateSignature(
                pspSecret,
                timestamp,
                requestBody
        );

        appLoggerService.info(
                LogStrings.Feature.PLUGIN_SYNC,
                LogStrings.Action.PSP_SYNC_REQUEST_SENT,
                "pluginCode={} methodCodes={}",
                manifest.pluginCode(),
                request.methods()
                        .stream()
                        .map(PluginPaymentMethodRegistrationRequest::code)
                        .toList()
        );

        PluginSyncResponse response = pspClient.syncPlugin(
                manifest.pluginCode(),
                timestamp,
                signature,
                requestBody
        );

        if (response == null) {
            throw new IllegalStateException("PSP sync response is empty.");
        }

        appLoggerService.info(
                LogStrings.Feature.PLUGIN_SYNC,
                LogStrings.Action.PSP_SYNC_COMPLETED,
                "pluginCode={} paymentMethods={} message={}",
                response.pluginCode(),
                response.paymentMethods(),
                response.message()
        );
    }

    private PluginManifest loadManifest() {
        try {
            return objectMapper.readValue(
                    resourceLoader.getResource("classpath:" + manifestPath)
                            .getInputStream(),
                    PluginManifest.class
            );
        } catch (Exception exception) {
            throw new IllegalStateException(LogStrings.Reason.MANIFEST_READ_FAILED);
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(LogStrings.Reason.JSON_SERIALIZATION_FAILED);
        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(syncRetryDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread()
                    .interrupt();

            throw new IllegalStateException("PSP sync retry interrupted.");
        }
    }

}