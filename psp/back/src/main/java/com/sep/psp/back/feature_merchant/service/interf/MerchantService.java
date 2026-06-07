package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.MerchantProfileResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.dto.RegenerateMerchantPasswordResponse;
import com.sep.psp.back.feature_merchant.dto.UpdateMerchantProfileRequest;

public interface MerchantService {

    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);

    MerchantProfileResponse getCurrentMerchantProfile();

    void updateCurrentMerchantProfile(UpdateMerchantProfileRequest request);

    RegenerateMerchantPasswordResponse regenerateMerchantPassword();

}