package com.sep.psp.back.feature_payment.controller;

import com.sep.psp.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.psp.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.psp.back.feature_payment.dto.PaymentTransactionResponse;
import com.sep.psp.back.feature_payment.service.interf.PaymentTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Payment transactions",
        description = "Endpoints for creating and reading PSP payment transactions."
)
@RestController
@RequestMapping("/api/payments")
public class PaymentTransactionController {

    @Autowired
    PaymentTransactionService paymentTransactionService;

    @Operation(
            summary = "Create PSP payment transaction",
            description = """
                    Creates a PSP payment transaction from a web shop payment initialization request.
                    The web shop authenticates using merchant ID and merchant password/API key.
                    """
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponse createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return paymentTransactionService.createPayment(request);
    }

    @Operation(
            summary = "Get PSP payment transaction",
            description = """
                    Returns basic payment transaction data needed by the PSP payment page.
                    """
    )
    @GetMapping("/{paymentId}")
    public PaymentTransactionResponse getPayment(
            @PathVariable String paymentId
    ) {
        return paymentTransactionService.getPayment(paymentId);
    }

}