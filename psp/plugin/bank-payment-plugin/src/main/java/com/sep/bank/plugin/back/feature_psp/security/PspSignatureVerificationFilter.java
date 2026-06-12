package com.sep.bank.plugin.back.feature_psp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_psp.dto.manifest.PluginManifest;
import com.sep.bank.plugin.back.feature_psp.service.interf.PspHmacService;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
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
public class PspSignatureVerificationFilter extends OncePerRequestFilter {

    private static final String PLUGIN_HEARTBEAT_PATH = "/api/plugin/heartbeat";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.plugin.secret}")
    String pluginSecret;

    @Value("${app.plugin.manifest-path}")
    String manifestPath;

    @Value("${app.plugin.signature-max-timestamp-age-minutes:5}")
    long maxTimestampAgeMinutes;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    PspHmacService pspHmacService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !isSignedPspEndpoint(request.getServletPath());
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
            verifyPspRequest(
                    cachedRequest,
                    requestBody
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

    private void verifyPspRequest(
            HttpServletRequest request,
            String requestBody
    ) {
        String pluginCodeHeader = request.getHeader(PspSecurityHeaders.PLUGIN_CODE);
        String timestamp = request.getHeader(PspSecurityHeaders.TIMESTAMP);
        String signature = request.getHeader(PspSecurityHeaders.SIGNATURE);

        validateRequiredHeaders(
                pluginCodeHeader,
                timestamp,
                signature
        );

        validatePluginCode(pluginCodeHeader);
        validateTimestamp(timestamp);

        validateSignature(
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

    private void validatePluginCode(String pluginCodeHeader) {
        PluginManifest manifest = loadManifest();

        if (!manifest.pluginCode().equals(pluginCodeHeader)) {
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
                throw new IllegalArgumentException("PSP request timestamp is not valid.");
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException("PSP request timestamp is not valid.");
        }
    }

    private void validateSignature(
            String timestamp,
            String requestBody,
            String signature
    ) {
        boolean signatureValid = pspHmacService.isSignatureValid(
                pluginSecret,
                timestamp,
                requestBody,
                signature
        );

        if (!signatureValid) {
            throw new IllegalArgumentException("Invalid PSP request signature.");
        }
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

    private boolean isSignedPspEndpoint(String servletPath) {
        return PLUGIN_HEARTBEAT_PATH.equals(servletPath);
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