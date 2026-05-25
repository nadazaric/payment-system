package com.sep.psp.back.feature_merchant.repository;

import com.sep.psp.back.feature_merchant.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, String> {
}