package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationResponse;
import com.sep.psp.back.feature_plugin.service.interf.PluginConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PluginConfigurationServiceImpl implements PluginConfigurationService {

    private static final String CONFIGURATION_ENDPOINT = "/api/plugin/configurations";

    @Autowired
    PluginHttpClient pluginHttpClient;

    @Override
    public PluginConfigurationResponse configurePaymentMethod(
            PaymentMethod paymentMethod,
            Merchant merchant,
            MerchantSellerAccount sellerAccount,
            Map<String, String> values
    ) {
        PluginConfigurationRequest request = new PluginConfigurationRequest(
                merchant.getMerchantId(),
                sellerAccount.getSellerReference(),
                paymentMethod.getCode(),
                values
        );

        return pluginHttpClient.post(
                paymentMethod.getPlugin(),
                CONFIGURATION_ENDPOINT,
                request,
                PluginConfigurationResponse.class
        );
    }

}