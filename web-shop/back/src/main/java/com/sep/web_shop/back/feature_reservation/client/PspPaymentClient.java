package com.sep.web_shop.back.feature_reservation.client;

import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentRequest;
import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PspPaymentClient {

    private final RestClient restClient;

    public PspPaymentClient(
            @Value("${app.psp.base-url}") String pspBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(pspBaseUrl)
                .build();
    }

    public PspCreatePaymentResponse createPayment(PspCreatePaymentRequest request) {
        return restClient.post()
                .uri("/api/payments")
                .body(request)
                .retrieve()
                .body(PspCreatePaymentResponse.class);
    }

}