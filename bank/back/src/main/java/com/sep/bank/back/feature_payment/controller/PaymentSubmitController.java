package com.sep.bank.back.feature_payment.controller;

import com.sep.bank.back.feature_payment.dto.CardPaymentSubmitRequest;
import com.sep.bank.back.feature_payment.service.interf.CardPaymentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank/payments")
public class PaymentSubmitController {

    @Autowired
    CardPaymentProcessingService cardPaymentProcessingService;

    @PostMapping(
            value = "/{paymentId}/submit",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ResponseEntity<Void> submitPayment(
            @PathVariable UUID paymentId,
            @RequestParam String pan,
            @RequestParam String securityCode,
            @RequestParam String cardHolderName,
            @RequestParam String expirationDate
    ) {
        CardPaymentSubmitRequest request = new CardPaymentSubmitRequest(
                pan,
                securityCode,
                cardHolderName,
                expirationDate
        );

        String redirectUrl = cardPaymentProcessingService.submitCardPayment(paymentId, request);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(redirectUrl))
                .build();
    }

}