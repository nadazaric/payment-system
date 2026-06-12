package com.sep.bank.plugin.back.feature_payment.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentResponse;
import com.sep.bank.plugin.back.shared.security.SignatureHeaders;
import com.sep.bank.plugin.back.shared.security.service.interf.HmacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Component
public class BankClient {

    private final RestClient restClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    HmacService hmacService;

    @Value("${app.security.bank-secret}")
    String bankSecret;

    @Value("${app.security.plugin-code}")
    String pluginCode;

    public BankClient(
            @Value("${app.bank.base-url}") String bankBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(bankBaseUrl)
                .build();
    }

    public CreateBankPaymentResponse createPayment(
            CreateBankPaymentRequest request
    ) {
        String requestBody = writeJson(request);
        String timestamp = Instant.now().toString();

        String signature = hmacService.generateSignature(
                bankSecret,
                timestamp,
                requestBody
        );

        return restClient.post()
                .uri("/api/bank/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header(SignatureHeaders.PLUGIN_CODE, pluginCode)
                .header(SignatureHeaders.TIMESTAMP, timestamp)
                .header(SignatureHeaders.SIGNATURE, signature)
                .body(requestBody)
                .retrieve()
                .body(CreateBankPaymentResponse.class);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not serialize bank payment request.");
        }
    }

}