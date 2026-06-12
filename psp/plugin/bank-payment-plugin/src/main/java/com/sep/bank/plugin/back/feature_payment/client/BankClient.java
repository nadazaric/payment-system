package com.sep.bank.plugin.back.feature_payment.client;

import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BankClient {

    private final RestClient restClient;

    public BankClient(@Value("${app.bank.base-url}") String bankBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(bankBaseUrl)
                .build();
    }

    public CreateBankPaymentResponse createPayment(
            CreateBankPaymentRequest request
    ) {
        return restClient.post()
                .uri("/api/bank/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CreateBankPaymentResponse.class);
    }

}