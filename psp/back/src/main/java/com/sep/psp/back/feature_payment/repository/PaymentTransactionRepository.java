package com.sep.psp.back.feature_payment.repository;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    boolean existsByMerchantAndMerchantOrderId(Merchant merchant, String merchantOrderId);

}