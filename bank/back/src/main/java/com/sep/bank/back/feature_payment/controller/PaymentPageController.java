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
            return noCacheResponse(HttpStatus.NOT_FOUND).body(paymentPageRenderer.renderNotFoundPage());
        }

        Optional<PaymentPageDTO> paymentPageOptional = paymentService.getPaymentPageData(parsedPaymentId);

        if (paymentPageOptional.isEmpty()) {
            return noCacheResponse(HttpStatus.NOT_FOUND).body(paymentPageRenderer.renderNotFoundPage());
        }

        return noCacheResponse(HttpStatus.OK).body(paymentPageRenderer.renderPaymentPage(paymentPageOptional.get()));
    }

    private ResponseEntity.BodyBuilder noCacheResponse(HttpStatus status) {
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentType(MediaType.TEXT_HTML);
    }

}