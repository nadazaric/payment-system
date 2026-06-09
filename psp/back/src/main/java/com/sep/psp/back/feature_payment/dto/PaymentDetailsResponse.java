package com.sep.psp.back.feature_payment.dto;

import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Payment transaction details shown on the PSP payment page.")
public record PaymentDetailsResponse(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId,

        @Schema(
                description = "Merchant display name.",
                example = "Vehicle Rental Agency"
        )
        String merchantName,

        @Schema(
                description = "Seller reference inside the merchant account.",
                example = "MAIN_SELLER"
        )
        String sellerReference,

        @Schema(
                description = "Seller display name.",
                example = "Main seller"
        )
        String sellerDisplayName,

        @Schema(
                description = "Payment amount.",
                example = "105.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Payment currency.",
                example = "EUR"
        )
        String currency,

        @Schema(
                description = "Current PSP payment transaction status.",
                example = "CREATED"
        )
        PaymentStatus status,

        @Schema(
                description = "Selected payment method code. Null until customer selects a payment method.",
                example = "MOCK_PAY",
                nullable = true
        )
        String selectedPaymentMethodCode,

        @Schema(
                description = "Payment methods available for this seller and transaction."
        )
        List<PaymentOptionResponse> paymentMethods
) {
}