package com.sep.psp.back.feature_merchant.mapper;

import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

}