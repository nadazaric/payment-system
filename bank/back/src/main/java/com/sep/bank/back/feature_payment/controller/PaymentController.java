package com.sep.bank.back.feature_payment.controller;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.bank.back.feature_payment.service.interf.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank/payments")
@Tag(
        name = "Payments",
        description = "Bank payment session API."
)
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping
    @Operation(
            summary = "Create bank payment session",
            description = "Creates bank payment session and returns bank payment ID and bank payment page URL."
    )
    public CreatePaymentResponse createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.createPayment(request);
    }

}