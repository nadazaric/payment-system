package com.sep.web_shop.back.feature_reservation.client;

import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentRequest;
import com.sep.web_shop.back.feature_reservation.dto.PspCreatePaymentResponse;
import com.sep.web_shop.back.shared.logging.LogStrings;
import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PspPaymentClient {

    private final RestClient restClient;

    @Autowired
    AppLoggerService appLoggerService;

    public PspPaymentClient(
            @Value("${app.psp.base-url}") String pspBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(pspBaseUrl)
                .build();
    }

    public PspCreatePaymentResponse createPayment(PspCreatePaymentRequest request) {
        try {
            appLoggerService.info(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REQUEST_SENT,
                    "merchantId={} sellerReference={} merchantOrderId={} amount={} currency={}",
                    request.merchantId(),
                    request.sellerReference(),
                    request.merchantOrderId(),
                    request.amount(),
                    request.currency()
            );

            PspCreatePaymentResponse response = restClient.post()
                    .uri("/api/payments")
                    .body(request)
                    .retrieve()
                    .body(PspCreatePaymentResponse.class);

            return response;

        } catch (RestClientException exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} merchantId={} sellerReference={} merchantOrderId={} error={}",
                    LogStrings.Reason.PSP_PAYMENT_CREATE_FAILED,
                    request.merchantId(),
                    request.sellerReference(),
                    request.merchantOrderId(),
                    exception.getMessage()
            );

            throw exception;
        }
    }

}