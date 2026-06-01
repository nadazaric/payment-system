package com.sep.psp.back.feature_payment.mapper;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    @Mapping(source = "plugin.code", target = "pluginCode")
    PaymentMethodResponse toPaymentMethodResponse(PaymentMethod paymentMethod);

    List<PaymentMethodResponse> toPaymentMethodResponseList(List<PaymentMethod> paymentMethods);

}