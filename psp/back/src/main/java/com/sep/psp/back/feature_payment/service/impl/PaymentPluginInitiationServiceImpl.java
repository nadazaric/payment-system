package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.PluginPaymentInitiationRequest;
import com.sep.psp.back.feature_payment.dto.PluginPaymentInitiationResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.service.interf.PaymentPluginInitiationService;
import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentPluginInitiationServiceImpl implements PaymentPluginInitiationService {

    private static final String PAYMENT_INITIATION_ENDPOINT = "/api/plugin/payments/initiate";

    @Autowired
    PluginHttpClient pluginHttpClient;

    @Override
    public PluginPaymentInitiationResponse initiatePayment(
            PaymentTransaction paymentTransaction,
            MerchantSellerPaymentMethod sellerPaymentMethod
    ) {
        PluginPaymentInitiationRequest request = new PluginPaymentInitiationRequest(
                paymentTransaction.getId(),
                paymentTransaction.getMerchant().getMerchantId(),
                paymentTransaction.getSellerAccount().getSellerReference(),
                sellerPaymentMethod.getPaymentMethod().getCode(),
                paymentTransaction.getAmount(),
                paymentTransaction.getCurrency(),
                paymentTransaction.getMerchantOrderId()
        );

        return pluginHttpClient.post(
                sellerPaymentMethod.getPaymentMethod().getPlugin(),
                PAYMENT_INITIATION_ENDPOINT,
                request,
                PluginPaymentInitiationResponse.class
        );
    }

}