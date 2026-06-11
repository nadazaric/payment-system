package com.sep.bank.back.feature_payment.repository;

import com.sep.bank.back.feature_payment.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {

    boolean existsByBankMerchantId(String bankMerchantId);

}