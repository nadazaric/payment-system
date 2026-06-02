package com.sep.psp.back.feature_merchant.mapper;

import com.sep.psp.back.feature_merchant.dto.MerchantProfileResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantRegistrationResponse;
import com.sep.psp.back.feature_merchant.dto.MerchantSellerAccountResponse;
import com.sep.psp.back.feature_merchant.dto.SellerPaymentMethodResponse;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
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

    default MerchantSellerAccountResponse toSellerAccountResponse(
            MerchantSellerAccount sellerAccount
    ) {
        return new MerchantSellerAccountResponse(
                sellerAccount.getId(),
                sellerAccount.getSellerReference(),
                sellerAccount.getDisplayName(),
                sellerAccount.getActive(),
                toSellerPaymentMethodResponseList(sellerAccount.getPaymentMethods())
        );
    }

    default List<MerchantSellerAccountResponse> toSellerAccountResponseList(
            List<MerchantSellerAccount> sellerAccounts
    ) {
        return sellerAccounts.stream()
                .map(this::toSellerAccountResponse)
                .toList();
    }

    default List<SellerPaymentMethodResponse> toSellerPaymentMethodResponseList(
            List<MerchantSellerPaymentMethod> paymentMethods
    ) {
        return paymentMethods.stream()
                .filter(this::shouldShowSellerPaymentMethod)
                .map(this::toSellerPaymentMethodResponse)
                .toList();
    }

    default SellerPaymentMethodResponse toSellerPaymentMethodResponse(
            MerchantSellerPaymentMethod sellerPaymentMethod
    ) {
        return new SellerPaymentMethodResponse(
                sellerPaymentMethod.getPaymentMethod().getCode(),
                sellerPaymentMethod.getPaymentMethod().getDisplayName(),
                sellerPaymentMethod.getPaymentMethod().getPlugin().getCode(),
                !sellerPaymentMethod.getConfigured()
        );
    }

    default Boolean shouldShowSellerPaymentMethod(
            MerchantSellerPaymentMethod sellerPaymentMethod
    ) {
        return sellerPaymentMethod.getPaymentMethod().getActive()
                && sellerPaymentMethod.getPaymentMethod().getPlugin().getActive();
    }
}