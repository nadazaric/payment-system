package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginInitiationResponse;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;

public interface PaymentPluginInitiationService {

    PaymentPluginInitiationResponse initiatePayment(PaymentTransaction paymentTransaction, MerchantSellerPaymentMethod sellerPaymentMethod);

}