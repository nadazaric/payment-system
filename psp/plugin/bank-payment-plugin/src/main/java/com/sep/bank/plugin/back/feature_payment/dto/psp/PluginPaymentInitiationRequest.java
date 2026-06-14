package com.sep.bank.plugin.back.feature_payment.dto.psp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request sent by PSP to bank payment plugin to initiate selected payment method flow.")
public record PluginPaymentInitiationRequest(

        @Schema(
                description = "PSP payment transaction identifier.",
                example = "0422dcd4-1c87-41a2-8e71-d3176d41eedd"
        )
        @NotBlank
        String paymentId,

        @Schema(
                description = "Merchant identifier issued by PSP.",
                example = "MER-TEST0001"
        )
        @NotBlank
        String merchantId,

        @Schema(
                description = "Seller reference inside the merchant account.",
                example = "MAIN_SELLER"
        )
        @NotBlank
        String sellerReference,

        @Schema(
                description = "Selected payment method code.",
                example = "CARD"
        )
        @NotBlank
        String paymentMethodCode,

        @Schema(
                description = "Payment amount.",
                example = "105.00"
        )
        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @Schema(
                description = "Payment currency.",
                example = "RSD"
        )
        @NotBlank
        String currency,

        @Schema(
                description = "Merchant URL where the customer should be redirected after successful payment.",
                example = "http://localhost:3001/payment/success"
        )
        @NotBlank
        String successUrl,

        @Schema(
                description = "Merchant URL where the customer should be redirected after failed payment.",
                example = "http://localhost:3001/payment/failed"
        )
        @NotBlank
        String failUrl,

        @Schema(
                description = "Merchant URL where the customer should be redirected after payment error.",
                example = "http://localhost:3001/payment/error"
        )
        @NotBlank
        String errorUrl,

        @Schema(
                description = "PSP callback URL used by the plugin to report final payment result.",
                example = "http://localhost:8082/api/payments/0422dcd4-1c87-41a2-8e71-d3176d41eedd/plugin-callback"
        )
        @NotBlank
        String pspCallbackUrl
) {
}
