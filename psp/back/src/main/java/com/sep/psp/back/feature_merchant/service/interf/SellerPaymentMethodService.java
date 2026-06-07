package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodRequest;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodResponse;

public interface SellerPaymentMethodService {

    ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            ConfigureSellerPaymentMethodRequest request,
            String authenticatedUsername
    );

    void removeSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            String authenticatedUsername
    );

}