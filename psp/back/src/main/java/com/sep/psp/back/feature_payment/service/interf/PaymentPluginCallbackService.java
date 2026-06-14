package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackResponse;

public interface PaymentPluginCallbackService {

    PaymentPluginCallbackResponse processCallback(
            String paymentId,
            PaymentPluginCallbackRequest request,
            String pluginCode
    );

}