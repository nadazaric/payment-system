package com.sep.bank.plugin.back.feature_payment.controller;

import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackResponse;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationRequest;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationResponse;
import com.sep.bank.plugin.back.feature_payment.service.interf.BankPaymentCallbackService;
import com.sep.bank.plugin.back.feature_payment.service.interf.PaymentInitiationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plugin/payments")
@Tag(name = "Plugin Payments")
public class PaymentController {

    @Autowired
    PaymentInitiationService paymentInitiationService;

    @Autowired
    BankPaymentCallbackService bankPaymentCallbackService;

    @PostMapping("/initiate")
    @Operation(
            summary = "Initiate payment",
            description = "Initiates selected bank payment method flow."
    )
    public PluginPaymentInitiationResponse initiatePayment(@Valid @RequestBody PluginPaymentInitiationRequest request) {
        return paymentInitiationService.initiatePayment(request);
    }

    @PostMapping("/bank-callback")
    @Operation(description = "Bank payment callback request.")
    public BankPaymentCallbackResponse processBankCallback(
            @RequestBody BankPaymentCallbackRequest request
    ) {
        return bankPaymentCallbackService.processBankCallback(request);
    }

}