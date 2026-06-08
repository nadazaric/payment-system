package com.sep.psp.back.feature_payment.mapper;

import com.sep.psp.back.feature_payment.dto.PaymentTransactionResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    @Mapping(source = "id", target = "paymentId")
    @Mapping(source = "merchant.merchantName", target = "merchantName")
    @Mapping(source = "sellerAccount.sellerReference", target = "sellerReference")
    @Mapping(source = "sellerAccount.displayName", target = "sellerDisplayName")
    PaymentTransactionResponse toResponse(PaymentTransaction paymentTransaction);

}