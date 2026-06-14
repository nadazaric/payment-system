package com.sep.bank.plugin.back.feature_payment.repository;

import com.sep.bank.plugin.back.feature_payment.model.PluginPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PluginPaymentRepository extends JpaRepository<PluginPayment, UUID> {

    Optional<PluginPayment> findByPspPaymentId(String pspPaymentId);

    Optional<PluginPayment> findByBankPaymentId(String bankPaymentId);

}