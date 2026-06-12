package com.sep.bank.back.feature_payment.repository;

import com.sep.bank.back.feature_payment.model.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID> {

    Optional<PaymentCard> findByPanHash(String panHash);

}