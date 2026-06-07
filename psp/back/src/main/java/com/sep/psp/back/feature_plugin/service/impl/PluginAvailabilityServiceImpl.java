package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.service.interf.MerchantStatusService;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PluginAvailabilityServiceImpl implements PluginAvailabilityService {

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    MerchantStatusService merchantStatusService;

    @Override
    @Transactional
    public void markPluginActive(PaymentPlugin paymentPlugin) {
        if (paymentPlugin.isActive()) {
            return;
        }

        paymentPlugin.setActive(true);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        merchantStatusService.refreshStatusesForPlugin(savedPlugin);
    }

    @Override
    @Transactional
    public void markPluginInactive(PaymentPlugin paymentPlugin) {
        if (!paymentPlugin.isActive()) {
            return;
        }

        paymentPlugin.setActive(false);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        merchantStatusService.refreshStatusesForPlugin(savedPlugin);
    }

}