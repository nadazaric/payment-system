package com.sep.bank.back.feature_payment.repository;

import com.sep.bank.back.feature_payment.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

}