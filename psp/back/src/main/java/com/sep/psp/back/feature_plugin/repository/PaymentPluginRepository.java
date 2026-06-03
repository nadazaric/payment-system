package com.sep.psp.back.feature_plugin.repository;

import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentPluginRepository extends JpaRepository<PaymentPlugin, String> {

    List<PaymentPlugin> findByActiveByAdminTrueAndBaseUrlIsNotNull();

}