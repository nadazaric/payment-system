package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_plugin.model.PaymentPlugin;

public interface PluginAvailabilityService {

    void markPluginActive(PaymentPlugin paymentPlugin);

    void markPluginInactive(PaymentPlugin paymentPlugin);

}