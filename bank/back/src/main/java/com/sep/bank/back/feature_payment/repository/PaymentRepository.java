package com.sep.bank.back.feature_payment.repository;

import com.sep.bank.back.feature_payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    // Exists by STAN
    boolean existsByBankMerchantIdAndStanAndPspTimestamp(
            String bankMerchantId,
            String stan,
            LocalDateTime pspTimestamp
    );

}