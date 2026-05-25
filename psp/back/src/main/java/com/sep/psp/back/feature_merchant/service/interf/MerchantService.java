package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;

public interface MerchantService {

    MerchantRegistrationResponse registerMerchant(MerchantRegistrationRequest request);

}