package com.sep.psp.back.feature_plugin.client.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.security.PluginSecurityHeaders;
import com.sep.psp.back.feature_plugin.service.interf.PluginAvailabilityService;
import com.sep.psp.back.feature_plugin.service.interf.PluginHmacService;
import com.sep.psp.back.feature_plugin.service.interf.PluginSecretEncryptionService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Instant;

@Component
public class PluginHttpClientImpl implements PluginHttpClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate = createRestTemplate();

    @Autowired
    PluginSecretEncryptionService pluginSecretEncryptionService;

    @Autowired
    PluginAvailabilityService pluginAvailabilityService;

    @Autowired
    PluginHmacService pluginHmacService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public <T> T post(
            PaymentPlugin plugin,
            String path,
            Object request,
            Class<T> responseType
    ) {
        validatePlugin(plugin, true);

        String url = buildUrl(
                plugin.getBaseUrl(),
                path
        );

        String requestBody = serializeRequest(request);

        HttpHeaders headers = buildSignedHeaders(
                plugin,
                requestBody
        );

        HttpEntity<String> entity = new HttpEntity<>(
                requestBody,
                headers
        );

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            T responseBody = response.getBody();

            if (responseBody == null) {
                throw new BadRequestException("Payment plugin returned empty response.");
            }

            return responseBody;
        } catch (RestClientException exception) {
            pluginAvailabilityService.markPluginInactive(plugin);

            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_PLUGIN,
                    LogStrings.Action.PLUGIN_HEALTH_CHECK_FAILED,
                    "pluginCode={} url={} error={}",
                    plugin.getCode(),
                    url,
                    exception.getMessage()
            );

            throw new BadRequestException("Payment plugin request failed.");
        }
    }

    @Override
    public void postWithoutResponse(
            PaymentPlugin plugin,
            String path,
            Object request,
            boolean requireActive
    ) {
        validatePlugin(
                plugin,
                requireActive
        );

        String url = buildUrl(
                plugin.getBaseUrl(),
                path
        );

        String requestBody = serializeRequest(request);

        HttpHeaders headers = buildSignedHeaders(
                plugin,
                requestBody
        );

        HttpEntity<String> entity = new HttpEntity<>(
                requestBody,
                headers
        );

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );
        } catch (ResourceAccessException | HttpServerErrorException exception) {
            throw new BadRequestException("Payment plugin is not available.");
        } catch (RestClientException exception) {
            throw new BadRequestException("Payment plugin request failed.");
        }
    }

    private void validatePlugin(PaymentPlugin plugin, boolean requireActive) {
        if (plugin == null) {
            throw new BadRequestException("Payment plugin is required.");
        }

        if (!plugin.isActiveByAdmin()) {
            throw new BadRequestException("Payment plugin is disabled by PSP super admin.");
        }

        if (plugin.getBaseUrl() == null || plugin.getBaseUrl().isBlank()) {
            throw new BadRequestException("Payment plugin base URL is missing.");
        }

        if (requireActive && !plugin.isActive()) {
            throw new BadRequestException("Payment plugin is not active.");
        }
    }

    private String serializeRequest(Object request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            throw new BadRequestException("Could not serialize payment plugin request.");
        }
    }

    private HttpHeaders buildSignedHeaders(
            PaymentPlugin plugin,
            String requestBody
    ) {
        String timestamp = Instant.now().toString();

        String pluginSecret = pluginSecretEncryptionService.decrypt(
                plugin.getEncryptedPluginSecret()
        );

        String signature = pluginHmacService.generateSignature(
                pluginSecret,
                timestamp,
                requestBody
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(
                PluginSecurityHeaders.PLUGIN_CODE,
                plugin.getCode()
        );
        headers.add(
                PluginSecurityHeaders.TIMESTAMP,
                timestamp
        );
        headers.add(
                PluginSecurityHeaders.SIGNATURE,
                signature
        );

        return headers;
    }

    private String buildUrl(
            String baseUrl,
            String path
    ) {
        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(
                0,
                baseUrl.length() - 1
        )
                : baseUrl;

        String normalizedPath = path.startsWith("/")
                ? path
                : "/" + path;

        return normalizedBaseUrl + normalizedPath;
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);

        return new RestTemplate(requestFactory);
    }
}