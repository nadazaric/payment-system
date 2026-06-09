package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.PluginPaymentInitiationResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;

public interface PaymentPluginInitiationService {

    PluginPaymentInitiationResponse initiatePayment(PaymentTransaction paymentTransaction, MerchantSellerPaymentMethod sellerPaymentMethod);

}