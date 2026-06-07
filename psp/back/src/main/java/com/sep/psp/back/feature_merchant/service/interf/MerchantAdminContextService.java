package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.model.MerchantAdmin;

public interface MerchantAdminContextService {

    MerchantAdmin getAuthenticatedMerchantAdmin();

    MerchantAdmin getMerchantAdminByUsername(String username);

    String getAuthenticatedUsername();

}