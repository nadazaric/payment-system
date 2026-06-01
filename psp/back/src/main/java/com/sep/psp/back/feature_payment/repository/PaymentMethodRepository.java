package com.sep.psp.back.feature_payment.repository;

import com.sep.psp.back.feature_payment.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, String> {

    List<PaymentMethod> findByActiveTrueAndPlugin_ActiveTrue();

}