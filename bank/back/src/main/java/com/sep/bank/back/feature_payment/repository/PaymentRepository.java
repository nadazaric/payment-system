package com.sep.bank.back.feature_payment.repository;

import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    boolean existsByBankMerchantIdAndStanAndPspTimestamp(
            String bankMerchantId,
            String stan,
            LocalDateTime pspTimestamp
    );

    List<Payment> findByStatusAndPaymentAttemptUsedFalseAndExpiresAtBefore(PaymentStatus status, LocalDateTime expiresAt);

}