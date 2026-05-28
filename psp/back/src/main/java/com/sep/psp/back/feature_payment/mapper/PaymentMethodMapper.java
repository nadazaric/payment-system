package com.sep.psp.back.feature_payment.mapper;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodResponse toPaymentMethodResponse(PaymentMethod paymentMethod);

    List<PaymentMethodResponse> toPaymentMethodResponseList(List<PaymentMethod> paymentMethods);

}