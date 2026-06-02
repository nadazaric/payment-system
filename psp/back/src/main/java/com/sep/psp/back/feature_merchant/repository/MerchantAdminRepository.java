package com.sep.psp.back.feature_merchant.repository;

import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantAdminRepository extends JpaRepository<MerchantAdmin, String> {

    Optional<MerchantAdmin> findByUsername(String username);

    Boolean existsByUsername(String username);

}