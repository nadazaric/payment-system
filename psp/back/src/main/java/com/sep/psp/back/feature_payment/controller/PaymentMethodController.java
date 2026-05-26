package com.sep.psp.back.feature_payment.controller;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;
import com.sep.psp.back.feature_payment.service.interf.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Payment methods",
        description = "Endpoints for listing PSP payment methods."
)
@RestController
@RequestMapping("/api/payment-methods")
public class PaymentMethodController {

    @Autowired
    PaymentMethodService paymentMethodService;

    // ----------------------------------------------------------------------------------------------------------------- Get active payment methods
    @Operation(
            summary = "Get active payment methods",
            description = """
                    Returns globally active payment methods available in PSP.
                    Merchant admin can later assign these methods to seller accounts.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Active payment methods returned successfully."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid JWT token."
            )
    })
    @GetMapping
    public List<PaymentMethodResponse> getActivePaymentMethods() {
        return paymentMethodService.getActivePaymentMethods();
    }
}