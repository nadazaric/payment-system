package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.UpdateSellerPaymentMethodsRequest;

public interface SellerPaymentMethodService {

    void updateSellerPaymentMethods(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request,
            String authenticatedUsername
    );
}