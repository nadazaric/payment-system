package com.sep.psp.back.feature_merchant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request used to update available payment methods for a seller account.")
public record UpdateSellerPaymentMethodsRequest(

        @NotEmpty(message = "At least one payment method must be selected.")
        @Schema(
                description = "Payment method codes selected for the seller account.",
                example = "[\"CARD\", \"QR_CODE\"]"
        )
        List<String> paymentMethodCodes
) {
}