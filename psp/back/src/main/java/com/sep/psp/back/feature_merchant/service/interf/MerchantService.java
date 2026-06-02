package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.*;

import java.util.List;

public interface MerchantService {

    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);

    MerchantProfileResponse getCurrentMerchantProfile();

    List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts();

    MerchantSellerAccountResponse createSellerAccount(CreateMerchantSellerAccountRequest request);

    void updateSellerPaymentMethods(String sellerId, UpdateSellerPaymentMethodsRequest request);

    void updateCurrentMerchantProfile(UpdateMerchantProfileRequest request);

    RegenerateMerchantPasswordResponse regenerateMerchantPassword();

    void updateSellerAccount(String sellerId, UpdateMerchantSellerAccountRequest request);

    ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(String sellerId, String paymentMethodCode, ConfigureSellerPaymentMethodRequest request);

}