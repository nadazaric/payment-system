package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_payment.dto.PaymentMethodResponse;

import java.util.List;

public interface PaymentMethodService {

    List<PaymentMethodResponse> getActivePaymentMethods();

}