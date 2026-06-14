package com.sep.bank.plugin.back.feature_psp.repository;

import com.sep.bank.plugin.back.feature_psp.model.BankPluginSellerConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankPluginSellerConfigurationRepository extends JpaRepository<BankPluginSellerConfiguration, UUID> {

    Optional<BankPluginSellerConfiguration> findByMerchantIdAndSellerReferenceAndPaymentMethodCode(
            String merchantId,
            String sellerReference,
            String paymentMethodCode
    );

}
