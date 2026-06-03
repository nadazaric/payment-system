package com.sep.psp.back.feature_merchant.service.interf;

import com.sep.psp.back.feature_merchant.dto.CreateMerchantSellerAccountRequest;
import com.sep.psp.back.feature_merchant.dto.MerchantSellerAccountResponse;
import com.sep.psp.back.feature_merchant.dto.UpdateMerchantSellerAccountRequest;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;

import java.util.List;

public interface MerchantSellerService {

    MerchantSellerAccount createDefaultSellerAccount(Merchant merchant);

    List<MerchantSellerAccountResponse> getCurrentMerchantSellerAccounts();

    MerchantSellerAccountResponse createSellerAccount(CreateMerchantSellerAccountRequest request);

    void updateSellerAccount(String sellerId, UpdateMerchantSellerAccountRequest request);

}