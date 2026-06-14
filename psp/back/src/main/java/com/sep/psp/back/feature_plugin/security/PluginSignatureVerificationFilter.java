package com.sep.psp.back.feature_plugin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.psp.back.feature_plugin.dto.PluginSyncRequest;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginHmacService;
import com.sep.psp.back.feature_plugin.service.interf.PluginSecretEncryptionService;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class PluginSignatureVerificationFilter extends OncePerRequestFilter {

    private static final String PLUGIN_SYNC_PATH = "/api/plugins/sync";
    private static final String PLUGIN_PAYMENT_CALLBACK_SUFFIX = "/plugin-callback";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.plugin.encryption-max-timestamp-age-minutes:5}")
    long maxTimestampAgeMinutes;

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PluginSecretEncryptionService pluginSecretEncryptionService;

    @Autowired
    PluginHmacService pluginHmacService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !isSignedPluginEndpoint(request.getServletPath());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        String requestBody = cachedRequest.getCachedBodyAsString();

        try {
            verifyPluginRequest(
                    cachedRequest,
                    requestBody
            );

            filterChain.doFilter(
                    cachedRequest,
                    response
            );
        } catch (Exception exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_PLUGIN,
                    LogStrings.Action.PLUGIN_REQUEST_REJECTED,
                    "path={} pluginCode={} reason={}",
                    request.getServletPath(),
                    request.getHeader(PluginSecurityHeaders.PLUGIN_CODE),
                    exception.getMessage()
            );

            writeErrorResponse(response, exception.getMessage());
        }
    }

    private void verifyPluginRequest(
            HttpServletRequest request,
            String requestBody
    ) {
        String pluginCodeHeader = request.getHeader(PluginSecurityHeaders.PLUGIN_CODE);
        String timestamp = request.getHeader(PluginSecurityHeaders.TIMESTAMP);
        String signature = request.getHeader(PluginSecurityHeaders.SIGNATURE);

        validateRequiredHeaders(
                pluginCodeHeader,
                timestamp,
                signature
        );

        validateTimestamp(timestamp);

        if (PLUGIN_SYNC_PATH.equals(request.getServletPath())) {
            validateSyncRequestPluginCode(
                    requestBody,
                    pluginCodeHeader
            );
        }

        PaymentPlugin plugin = paymentPluginRepository.findById(pluginCodeHeader)
                .orElseThrow(() -> new IllegalArgumentException("Payment plugin is not expected by PSP."));

        if (!plugin.isActiveByAdmin()) {
            throw new IllegalArgumentException("Payment plugin is disabled by PSP super admin.");
        }

        validateSignature(
                plugin,
                timestamp,
                requestBody,
                signature
        );
    }

    private void validateRequiredHeaders(
            String pluginCodeHeader,
            String timestamp,
            String signature
    ) {
        if (pluginCodeHeader == null || pluginCodeHeader.isBlank()) {
            throw new IllegalArgumentException("Plugin code header is required.");
        }

        if (timestamp == null || timestamp.isBlank()) {
            throw new IllegalArgumentException("Timestamp header is required.");
        }

        if (signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("Signature header is required.");
        }
    }

    private void validateTimestamp(String timestamp) {
        try {
            Instant requestInstant = Instant.parse(timestamp);
            Instant now = Instant.now();

            Duration age = Duration.between(
                    requestInstant,
                    now
            ).abs();

            if (age.compareTo(Duration.ofMinutes(maxTimestampAgeMinutes)) > 0) {
                throw new IllegalArgumentException("Plugin request timestamp is not valid.");
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("Plugin request timestamp is not valid.");
        }
    }

    private void validateSyncRequestPluginCode(
            String requestBody,
            String pluginCodeHeader
    ) {
        PluginSyncRequest syncRequest = readSyncRequest(requestBody);

        if (!pluginCodeHeader.equals(syncRequest.pluginCode())) {
            throw new IllegalArgumentException("Plugin code header does not match request body.");
        }
    }

    private PluginSyncRequest readSyncRequest(String requestBody) {
        try {
            return objectMapper.readValue(
                    requestBody,
                    PluginSyncRequest.class
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid plugin sync request body.");
        }
    }

    private void validateSignature(
            PaymentPlugin plugin,
            String timestamp,
            String requestBody,
            String signature
    ) {
        String pluginSecret = pluginSecretEncryptionService.decrypt(
                plugin.getEncryptedPluginSecret()
        );

        boolean signatureValid = pluginHmacService.isSignatureValid(
                pluginSecret,
                timestamp,
                requestBody,
                signature
        );

        if (!signatureValid) {
            throw new IllegalArgumentException("Invalid plugin request signature.");
        }
    }

    private boolean isSignedPluginEndpoint(String servletPath) {
        return PLUGIN_SYNC_PATH.equals(servletPath)
                || isPaymentPluginCallbackPath(servletPath);
    }

    private boolean isPaymentPluginCallbackPath(String servletPath) {
        return servletPath.startsWith("/api/payments/")
                && servletPath.endsWith(PLUGIN_PAYMENT_CALLBACK_SUFFIX);
    }

    private void writeErrorResponse(
            HttpServletResponse response,
            String message
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        objectMapper.writeValue(
                response.getWriter(),
                Map.of("message", message)
        );
    }

}