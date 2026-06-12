package com.sep.bank.back.feature_payment.controller;

import com.sep.bank.back.feature_payment.dto.PaymentPageDTO;
import com.sep.bank.back.feature_payment.page.PaymentPageRenderer;
import com.sep.bank.back.feature_payment.service.interf.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentPageController {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentPageRenderer paymentPageRenderer;

    @GetMapping("/{paymentId}")
    public ResponseEntity<String> getPaymentPage(@PathVariable String paymentId) {
        UUID parsedPaymentId;

        try {
            parsedPaymentId = UUID.fromString(paymentId);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.TEXT_HTML)
                    .body(paymentPageRenderer.renderNotFoundPage());
        }

        Optional<PaymentPageDTO> paymentPageOptional = paymentService.getPaymentPageData(parsedPaymentId);

        return paymentPageOptional.map(paymentPageDTO -> ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(paymentPageRenderer.renderPaymentPage(paymentPageDTO))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(paymentPageRenderer.renderNotFoundPage()));

    }

}