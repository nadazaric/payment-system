package com.sep.psp.back.feature_merchant.repository;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MerchantSellerAccountRepository extends JpaRepository<MerchantSellerAccount, String> {

    List<MerchantSellerAccount> findByMerchant(Merchant merchant);

    boolean existsByMerchantAndSellerReference(Merchant merchant, String sellerReference);

    Optional<MerchantSellerAccount> findByMerchantAndSellerReference(Merchant merchant, String sellerReference);

}