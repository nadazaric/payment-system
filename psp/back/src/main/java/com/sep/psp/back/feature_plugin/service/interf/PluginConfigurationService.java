package com.sep.psp.back.feature_plugin.service.interf;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationResponse;

import java.util.Map;

public interface  PluginConfigurationService {

    PluginConfigurationResponse configurePaymentMethod(
            PaymentMethod paymentMethod,
            Merchant merchant,
            MerchantSellerAccount sellerAccount,
            Map<String, String> values
    );
}
