package com.sep.psp.back.feature_payment.dto.plugin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Request sent by PSP to payment plugin to initiate selected payment method flow.")
public record PaymentPluginInitiationRequest(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        String paymentId,

        @Schema(
                description = "Merchant identifier issued by PSP.",
                example = "MER-TEST0001"
        )
        String merchantId,

        @Schema(
                description = "Seller reference inside the merchant account.",
                example = "MAIN_SELLER"
        )
        String sellerReference,

        @Schema(
                description = "Selected payment method code.",
                example = "MOCK_PAY"
        )
        String paymentMethodCode,

        @Schema(
                description = "Payment amount.",
                example = "105.00"
        )
        BigDecimal amount,

        @Schema(
                description = "Payment currency.",
                example = "RSD"
        )
        String currency,

        @Schema(
                description = "Merchant URL where the customer should be redirected after successful payment.",
                example = "http://localhost:3001/payment/success"
        )
        String successUrl,

        @Schema(
                description = "Merchant URL where the customer should be redirected after failed payment.",
                example = "http://localhost:3001/payment/failed"
        )
        String failUrl,

        @Schema(
                description = "Merchant URL where the customer should be redirected after payment error.",
                example = "http://localhost:3001/payment/error"
        )
        String errorUrl,

        @Schema(
                description = "PSP callback URL used by the plugin to report final payment result.",
                example = "http://localhost:8082/api/payments/0422dcd4-1c87-41a2-8e71-d3176d41eedd/plugin-callback"
        )
        String pspCallbackUrl
) {
}