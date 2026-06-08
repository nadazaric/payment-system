package com.sep.psp.back.feature_payment.mapper;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.PaymentOptionResponse;
import com.sep.psp.back.feature_payment.dto.PaymentTransactionResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    default PaymentTransactionResponse toResponse(PaymentTransaction paymentTransaction) {
        return new PaymentTransactionResponse(
                paymentTransaction.getId(),
                paymentTransaction.getMerchant().getMerchantName(),
                paymentTransaction.getSellerAccount().getSellerReference(),
                paymentTransaction.getSellerAccount().getDisplayName(),
                paymentTransaction.getAmount(),
                paymentTransaction.getCurrency(),
                paymentTransaction.getStatus(),
                toPaymentOptionResponseList(paymentTransaction.getSellerAccount().getPaymentMethods())
        );
    }

    default List<PaymentOptionResponse> toPaymentOptionResponseList(
            List<MerchantSellerPaymentMethod> sellerPaymentMethods
    ) {
        return sellerPaymentMethods.stream()
                .filter(MerchantSellerPaymentMethod::isAvailableForPayments)
                .map(this::toPaymentOptionResponse)
                .toList();
    }

    default PaymentOptionResponse toPaymentOptionResponse(
            MerchantSellerPaymentMethod sellerPaymentMethod
    ) {
        return new PaymentOptionResponse(
                sellerPaymentMethod.getPaymentMethod().getCode(),
                sellerPaymentMethod.getPaymentMethod().getDisplayName()
        );
    }

}