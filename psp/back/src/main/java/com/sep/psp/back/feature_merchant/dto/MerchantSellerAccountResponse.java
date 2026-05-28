package com.sep.psp.back.feature_merchant.dto;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Merchant seller account information.")
public record MerchantSellerAccountResponse(

        @Schema(
                description = "Seller account ID.",
                example = "seller-test-001"
        )
        String id,

        @Schema(
                description = "Seller reference used by the merchant system.",
                example = "MAIN_SELLER"
        )
        String sellerReference,

        @Schema(
                description = "Seller display name.",
                example = "Main seller"
        )
        String displayName,

        @Schema(
                description = "Shows whether this seller account is active for payments.",
                example = "true"
        )
        boolean active,

        @Schema(description = "Payment methods currently available for this seller account.")
        List<PaymentMethodResponse> availablePaymentMethods

) {
}