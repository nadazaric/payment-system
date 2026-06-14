package com.sep.bank.back.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import com.sep.bank.back.shared.security.service.interf.HmacService;
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
public class SignatureVerificationFilter extends OncePerRequestFilter {

    private static final String CREATE_PAYMENT_PATH = "/api/bank/payments";
    private static final String STATUS_CHECK_PATH = "/api/bank/payments/status-check";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.security.expected-plugin-code}")
    String expectedPluginCode;

    @Value("${app.security.plugin-secret}")
    String pluginSecret;

    @Value("${app.security.signature-max-timestamp-age-minutes:5}")
    long maxTimestampAgeMinutes;

    @Autowired
    HmacService hmacService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();

        return !HttpMethod.POST.matches(request.getMethod())
                || (!CREATE_PAYMENT_PATH.equals(servletPath)
                && !STATUS_CHECK_PATH.equals(servletPath));
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
            verifySignedRequest(
                    cachedRequest,
                    requestBody
            );

            filterChain.doFilter(
                    cachedRequest,
                    response
            );
        } catch (Exception exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.REQUEST_REJECTED,
                    "path={} pluginCode={} reason={}",
                    request.getServletPath(),
                    request.getHeader(SignatureHeaders.PLUGIN_CODE),
                    exception.getMessage()
            );

            writeErrorResponse(response, exception.getMessage());
        }
    }

    private void verifySignedRequest(
            HttpServletRequest request,
            String requestBody
    ) {
        String pluginCodeHeader = request.getHeader(SignatureHeaders.PLUGIN_CODE);
        String timestamp = request.getHeader(SignatureHeaders.TIMESTAMP);
        String signature = request.getHeader(SignatureHeaders.SIGNATURE);

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
        if (!expectedPluginCode.equals(pluginCodeHeader)) {
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
            String signature
    ) {
        boolean signatureValid = hmacService.isSignatureValid(
                pluginSecret,
                timestamp,
                requestBody,
                signature
        );

        if (!signatureValid) {
            throw new IllegalArgumentException("Invalid request signature.");
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

}