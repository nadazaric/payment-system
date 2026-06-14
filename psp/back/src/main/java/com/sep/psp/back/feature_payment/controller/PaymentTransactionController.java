package com.sep.psp.back.feature_payment.controller;

import com.sep.psp.back.feature_payment.dto.*;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackResponse;
import com.sep.psp.back.feature_payment.service.interf.PaymentPluginCallbackService;
import com.sep.psp.back.feature_payment.service.interf.PaymentTransactionService;
import com.sep.psp.back.feature_plugin.security.PluginSecurityHeaders;
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

    @Autowired
    PaymentPluginCallbackService paymentPluginCallbackService;

    // ----------------------------------------------------------------------------------------------------------------- Create Transaction by WS
    @Operation(
            summary = "Create PSP payment transaction",
            description = "Creates a PSP payment transaction from a web shop payment initialization request."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePaymentResponse createPayment(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return paymentTransactionService.createPayment(request);
    }

    // ----------------------------------------------------------------------------------------------------------------- Get Transaction Info
    @Operation(
            summary = "Get PSP payment transaction",
            description = "Returns basic payment transaction data needed by the PSP payment page."
    )
    @GetMapping("/{paymentId}")
    public PaymentDetailsResponse getPayment(
            @PathVariable String paymentId
    ) {
        return paymentTransactionService.getPayment(paymentId);
    }

    // ----------------------------------------------------------------------------------------------------------------- Init Transaction (PSP -> Plugin)
    @Operation(
            summary = "Initiate PSP payment",
            description = """
            Initiates a PSP payment using the selected payment method.
            PSP calls the corresponding payment plugin and returns the redirect URL to the PSP frontend.
            """
    )
    @PostMapping("/{paymentId}/initiate")
    public InitiatePaymentResponse initiatePayment(
            @PathVariable String paymentId,
            @Valid @RequestBody InitiatePaymentRequest request
    ) {
        return paymentTransactionService.initiatePayment(
                paymentId,
                request
        );
    }

    // ----------------------------------------------------------------------------------------------------------------- Receive Callback from Plugin
    @Operation(
            summary = "Process payment plugin callback",
            description = "Processes the final payment result reported by a payment plugin."
    )
    @PostMapping("/{paymentId}/plugin-callback")
    public PaymentPluginCallbackResponse processPaymentPluginCallback(
            @PathVariable String paymentId,
            @Valid @RequestBody PaymentPluginCallbackRequest request,
            @RequestHeader(PluginSecurityHeaders.PLUGIN_CODE) String pluginCode
    ) {
        return paymentPluginCallbackService.processCallback(
                paymentId,
                request,
                pluginCode
        );
    }

}