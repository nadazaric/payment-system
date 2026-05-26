package com.sep.psp.back.feature_merchant.mapper;

import com.sep.psp.back.feature_merchant.dto.MerchantProfileResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantSellerAccountResponse;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MerchantMapper {

    @Mapping(source = "merchantAdmin.username", target = "adminUsername")
    @Mapping(source = "defaultSellerAccount.id", target = "defaultSellerId")
    @Mapping(source = "defaultSellerAccount.sellerReference", target = "defaultSellerReference")
    MerchantRegistrationResponse toRegistrationResponse(
            Merchant merchant,
            MerchantAdmin merchantAdmin,
            MerchantSellerAccount defaultSellerAccount,
            String merchantPassword
    );

    @Mapping(source = "merchant.merchantId", target = "merchantId")
    @Mapping(source = "merchant.merchantName", target = "merchantName")
    @Mapping(source = "merchant.currency", target = "currency")
    @Mapping(source = "merchant.successUrl", target = "successUrl")
    @Mapping(source = "merchant.failUrl", target = "failUrl")
    @Mapping(source = "merchant.errorUrl", target = "errorUrl")
    @Mapping(source = "merchant.active", target = "merchantActive")
    @Mapping(source = "username", target = "adminUsername")
    @Mapping(source = "name", target = "adminName")
    MerchantProfileResponse toProfileResponse(MerchantAdmin merchantAdmin);

    MerchantSellerAccountResponse toSellerAccountResponse(MerchantSellerAccount sellerAccount);

    List<MerchantSellerAccountResponse> toSellerAccountResponseList(List<MerchantSellerAccount> sellerAccounts);

}