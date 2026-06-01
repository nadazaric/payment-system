package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodRequest;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodResponse;
import com.sep.psp.back.feature_merchant.dto.UpdateSellerPaymentMethodsRequest;

public interface SellerPaymentMethodService {

    void updateSellerPaymentMethods(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request,
            String authenticatedUsername
    );

    ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            ConfigureSellerPaymentMethodRequest request,
            String authenticatedUsername
    );
}