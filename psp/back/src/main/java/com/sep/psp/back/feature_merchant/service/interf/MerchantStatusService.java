package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;

import java.util.List;

public interface MerchantStatusService {

    void refreshSellerAndMerchantStatus(MerchantSellerAccount sellerAccount);

    void refreshStatusesForSellerPaymentMethods(List<MerchantSellerPaymentMethod> sellerPaymentMethods);

    void refreshStatusesForPaymentMethod(PaymentMethod paymentMethod);

    void refreshStatusesForPlugin(PaymentPlugin paymentPlugin);

}