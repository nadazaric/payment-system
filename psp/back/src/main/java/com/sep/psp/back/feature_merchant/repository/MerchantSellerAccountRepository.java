package com.sep.psp.back.feature_merchant.repository;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MerchantSellerAccountRepository extends JpaRepository<MerchantSellerAccount, String> {

    List<MerchantSellerAccount> findByMerchant(Merchant merchant);

    Boolean existsByMerchantAndSellerReference(Merchant merchant, String sellerReference);

}