package com.sep.bank.plugin.back.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_psp.dto.manifest.PluginManifest;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.security.service.interf.HmacService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class SignatureVerificationFilter extends OncePerRequestFilter {

    private static final String PLUGIN_HEARTBEAT_PATH = "/api/plugin/heartbeat";
    private static final String PLUGIN_CONFIGURATION_PATH = "/api/plugin/configurations";
    private static final String PLUGIN_PAYMENT_INITIATE_PATH = "/api/plugin/payments/initiate";
    private static final String PLUGIN_BANK_CALLBACK_PATH = "/api/plugin/payments/bank-callback";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.security.psp-secret}")
    String pspSecret;

    @Value("${app.security.bank-secret}")
    String bankSecret;

    @Value("${app.security.plugin-code}")
    String pluginCode;

    @Value("${app.plugin.manifest-path}")
    String manifestPath;

    @Value("${app.security.signature-max-timestamp-age-minutes:5}")
    long maxTimestampAgeMinutes;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    HmacService hmacService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || getSignedEndpointType(request.getServletPath()) == null;
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
            SignedEndpointType endpointType = getSignedEndpointType(cachedRequest.getServletPath());

            verifySignedRequest(
                    cachedRequest,
                    requestBody,
                    endpointType
            );

            filterChain.doFilter(
                    cachedRequest,
                    response
            );
        } catch (Exception exception) {
            writeErrorResponse(
                    response,
                    exception.getMessage()
            );
        }
    }

    private void verifySignedRequest(
            HttpServletRequest request,
            String requestBody,
            SignedEndpointType endpointType
    ) {
        String pluginCodeHeader = request.getHeader(SignatureHeaders.PLUGIN_CODE);
        String timestamp = request.getHeader(SignatureHeaders.TIMESTAMP);
        String signature = request.getHeader(SignatureHeaders.SIGNATURE);

        validateRequiredHeaders(
                pluginCodeHeader,
                timestamp,
                signature
        );

        validatePluginCode(
                pluginCodeHeader,
                endpointType
        );
        validateTimestamp(timestamp);
        validateSignature(
                timestamp,
                requestBody,
                signature,
                endpointType
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

    private void validatePluginCode(
            String pluginCodeHeader,
            SignedEndpointType endpointType
    ) {
        if (SignedEndpointType.PSP.equals(endpointType)) {
            PluginManifest manifest = loadManifest();

            if (!manifest.pluginCode().equals(pluginCodeHeader)) {
                throw new IllegalArgumentException("Plugin code header is not valid.");
            }

            return;
        }

        if (SignedEndpointType.BANK.equals(endpointType)
                && !pluginCode.equals(pluginCodeHeader)) {
            throw new IllegalArgumentException("Plugin code header is not valid.");
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
                throw new IllegalArgumentException("Signed request timestamp is not valid.");
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("Signed request timestamp is not valid.");
        }
    }

    private void validateSignature(
            String timestamp,
            String requestBody,
            String signature,
            SignedEndpointType endpointType
    ) {
        boolean signatureValid = hmacService.isSignatureValid(
                getSecret(endpointType),
                timestamp,
                requestBody,
                signature
        );

        if (!signatureValid) {
            throw new IllegalArgumentException("Invalid request signature.");
        }
    }

    private String getSecret(SignedEndpointType endpointType) {
        if (SignedEndpointType.PSP.equals(endpointType)) {
            return pspSecret;
        }

        return bankSecret;
    }

    private SignedEndpointType getSignedEndpointType(String servletPath) {
        if (PLUGIN_HEARTBEAT_PATH.equals(servletPath)
                || PLUGIN_CONFIGURATION_PATH.equals(servletPath)
                || PLUGIN_PAYMENT_INITIATE_PATH.equals(servletPath)) {
            return SignedEndpointType.PSP;
        }

        if (PLUGIN_BANK_CALLBACK_PATH.equals(servletPath)) {
            return SignedEndpointType.BANK;
        }

        return null;
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

    private enum SignedEndpointType {
        PSP,
        BANK
    }

}