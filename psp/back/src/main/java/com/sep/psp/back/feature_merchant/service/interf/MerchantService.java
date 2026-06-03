package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.*;

import java.util.List;

public interface MerchantService {

    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);

    MerchantProfileResponse getCurrentMerchantProfile();

    void updateSellerPaymentMethods(String sellerId, UpdateSellerPaymentMethodsRequest request);

    void updateCurrentMerchantProfile(UpdateMerchantProfileRequest request);

    RegenerateMerchantPasswordResponse regenerateMerchantPassword();

    ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(String sellerId, String paymentMethodCode, ConfigureSellerPaymentMethodRequest request);

}