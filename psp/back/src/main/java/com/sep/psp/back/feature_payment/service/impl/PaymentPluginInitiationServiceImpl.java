package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginInitiationRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginInitiationResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.service.interf.PaymentPluginInitiationService;
import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentPluginInitiationServiceImpl implements PaymentPluginInitiationService {

    private static final String PAYMENT_INITIATION_ENDPOINT = "/api/plugin/payments/initiate";

    @Value("${app.psp.backend-base-url}")
    String pspBackendBaseUrl;

    @Autowired
    PluginHttpClient pluginHttpClient;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public PaymentPluginInitiationResponse initiatePayment(
            PaymentTransaction paymentTransaction,
            MerchantSellerPaymentMethod sellerPaymentMethod
    ) {
        PaymentPluginInitiationRequest request = new PaymentPluginInitiationRequest(
                paymentTransaction.getId(),
                paymentTransaction.getMerchant().getMerchantId(),
                paymentTransaction.getSellerAccount().getSellerReference(),
                sellerPaymentMethod.getPaymentMethod().getCode(),
                paymentTransaction.getAmount(),
                paymentTransaction.getCurrency(),
                paymentTransaction.getMerchant().getSuccessUrl(),
                paymentTransaction.getMerchant().getFailUrl(),
                paymentTransaction.getMerchant().getErrorUrl(),
                buildPspCallbackUrl(paymentTransaction.getId())
        );

        PaymentPluginInitiationResponse response = pluginHttpClient.post(
                sellerPaymentMethod.getPaymentMethod().getPlugin(),
                PAYMENT_INITIATION_ENDPOINT,
                request,
                PaymentPluginInitiationResponse.class
        );

        validateRedirectUrl(
                paymentTransaction,
                sellerPaymentMethod,
                response
        );

        return response;
    }

    private String buildPspCallbackUrl(String paymentId) {
        return pspBackendBaseUrl + "/api/payments/" + paymentId + "/plugin-callback";
    }

    private void validateRedirectUrl(
            PaymentTransaction paymentTransaction,
            MerchantSellerPaymentMethod sellerPaymentMethod,
            PaymentPluginInitiationResponse response
    ) {
        if (response.redirectUrl() != null && !response.redirectUrl().isBlank()) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_INITIATE_REJECTED,
                "reason={} paymentId={} selectedPaymentMethodCode={} pluginCode={}",
                LogStrings.Reason.PLUGIN_REDIRECT_URL_MISSING,
                paymentTransaction.getId(),
                sellerPaymentMethod.getPaymentMethod().getCode(),
                sellerPaymentMethod.getPaymentMethod().getPlugin().getCode()
        );

        throw new BadRequestException("Payment plugin did not return redirect URL.");
    }

}