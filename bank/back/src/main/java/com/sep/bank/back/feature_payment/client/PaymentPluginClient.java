package com.sep.bank.back.feature_payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.back.feature_payment.dto.BankPaymentCallbackRequest;
import com.sep.bank.back.feature_payment.dto.BankPaymentCallbackResponse;
import com.sep.bank.back.shared.security.SignatureHeaders;
import com.sep.bank.back.shared.security.service.interf.HmacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class PaymentPluginClient {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HmacService hmacService;

    @Value("${app.security.expected-plugin-code}")
    String pluginCode;

    @Value("${app.security.plugin-secret}")
    String pluginSecret;

    public BankPaymentCallbackResponse sendPaymentCallback(
            String pluginCallbackUrl,
            BankPaymentCallbackRequest request
    ) {
        String requestBody = writeJson(request);
        String timestamp = Instant.now().toString();

        String signature = hmacService.generateSignature(
                pluginSecret,
                timestamp,
                requestBody
        );

        return RestClient.create()
                .post()
                .uri(pluginCallbackUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header(SignatureHeaders.PLUGIN_CODE, pluginCode)
                .header(SignatureHeaders.TIMESTAMP, timestamp)
                .header(SignatureHeaders.SIGNATURE, signature)
                .body(requestBody)
                .retrieve()
                .body(BankPaymentCallbackResponse.class);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Bank payment callback request could not be serialized.");
        }
    }

}