package com.sep.psp.back.feature_payment.repository;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    boolean existsByMerchantAndMerchantOrderId(Merchant merchant, String merchantOrderId);

    List<PaymentTransaction> findByStatusAndUpdatedAtBefore(PaymentStatus status, LocalDateTime updatedAt);

    List<PaymentTransaction> findByStatusAndSelectedPaymentMethodCodeIsNullAndCreatedAtBefore(
            PaymentStatus status,
            LocalDateTime createdAt
    );

}