package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.*;

import java.util.List;

public interface MerchantService {

    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);

    MerchantLoginResponse loginMerchantAdmin(MerchantLoginRequest request);

    MerchantProfileResponse getCurrentMerchantProfile();

    List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts();

    MerchantSellerAccountResponse createSellerAccount(CreateMerchantSellerAccountRequest request);

}