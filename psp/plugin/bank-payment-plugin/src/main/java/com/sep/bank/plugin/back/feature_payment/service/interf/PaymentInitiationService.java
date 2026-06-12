package com.sep.bank.plugin.back.feature_payment.service.interf;

import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationRequest;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationResponse;

public interface PaymentInitiationService {

    PluginPaymentInitiationResponse initiatePayment(
            PluginPaymentInitiationRequest request
    );

}