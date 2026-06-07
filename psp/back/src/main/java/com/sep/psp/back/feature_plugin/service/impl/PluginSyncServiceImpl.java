package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantStatusService;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.dto.PluginPaymentMethodRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginSyncRequest;
import com.sep.psp.back.feature_plugin.dto.PluginSyncResponse;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginAvailabilityService;
import com.sep.psp.back.feature_plugin.service.interf.PluginSyncService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PluginSyncServiceImpl implements PluginSyncService {

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    MerchantStatusService merchantStatusService;

    @Autowired
    PluginAvailabilityService pluginAvailabilityService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public PluginSyncResponse syncPlugin(PluginSyncRequest request) {
        String pluginCode = normalizePluginCode(request.pluginCode());

        PaymentPlugin plugin = paymentPluginRepository.findById(pluginCode)
                .orElseThrow(() -> new BadRequestException("Payment plugin is not expected by PSP."));

        if (!plugin.isActiveByAdmin()) {
            throw new BadRequestException("Payment plugin is disabled by PSP super admin.");
        }

        validateUniqueMethodCodes(request);

        plugin.setDisplayName(request.displayName());
        plugin.setBaseUrl(request.baseUrl());
        PaymentPlugin savedPlugin = paymentPluginRepository.save(plugin);
        pluginAvailabilityService.markPluginActive(savedPlugin);

        Set<String> synchronizedMethodCodes = new LinkedHashSet<>();

        for (PluginPaymentMethodRegistrationRequest methodRequest : request.methods()) {
            PaymentMethod paymentMethod = createOrUpdatePaymentMethod(
                    savedPlugin,
                    methodRequest
            );

            synchronizedMethodCodes.add(paymentMethod.getCode());

            if (methodRequest.updateRequired()) {
                markSellerConfigurationsAsRequired(paymentMethod);
            }
        }

        deactivateMethodsMissingFromManifest(
                savedPlugin,
                synchronizedMethodCodes
        );

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_PLUGIN,
                LogStrings.Action.PLUGIN_SYNC_COMPLETED,
                "pluginCode={} methodCodes={}",
                savedPlugin.getCode(),
                synchronizedMethodCodes
        );

        return new PluginSyncResponse(
                savedPlugin.getCode(),
                synchronizedMethodCodes.stream().toList(),
                "Plugin synchronized successfully."
        );
    }

    private void validateUniqueMethodCodes(PluginSyncRequest request) {
        Set<String> uniqueMethodCodes = new LinkedHashSet<>();

        for (PluginPaymentMethodRegistrationRequest methodRequest : request.methods()) {
            String paymentMethodCode = normalizePaymentMethodCode(methodRequest.code());

            if (!uniqueMethodCodes.add(paymentMethodCode)) {
                throw new BadRequestException("Duplicate payment method code in plugin manifest.");
            }
        }
    }

    private PaymentMethod createOrUpdatePaymentMethod(
            PaymentPlugin plugin,
            PluginPaymentMethodRegistrationRequest methodRequest
    ) {
        String paymentMethodCode = normalizePaymentMethodCode(methodRequest.code());

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodCode)
                .orElseGet(PaymentMethod::new);

        if (paymentMethod.getPlugin() != null
                && !paymentMethod.getPlugin().getCode().equals(plugin.getCode())) {
            throw new BadRequestException("Payment method code is already registered by another plugin.");
        }

        paymentMethod.setCode(paymentMethodCode);
        paymentMethod.setDisplayName(methodRequest.displayName());
        paymentMethod.setActive(methodRequest.active());
        paymentMethod.setPlugin(plugin);
        paymentMethod.setConfigSchemaJson(methodRequest.configSchemaJson());

        return paymentMethodRepository.save(paymentMethod);
    }

    private void deactivateMethodsMissingFromManifest(
            PaymentPlugin plugin,
            Set<String> synchronizedMethodCodes
    ) {
        List<PaymentMethod> existingPluginMethods = paymentMethodRepository.findByPlugin(plugin);

        List<PaymentMethod> removedMethods = existingPluginMethods.stream()
                .filter(paymentMethod -> !synchronizedMethodCodes.contains(paymentMethod.getCode()))
                .filter(PaymentMethod::isActive)
                .toList();

        removedMethods.forEach(paymentMethod -> {
            paymentMethod.setActive(false);
            paymentMethodRepository.save(paymentMethod);
            markSellerConfigurationsAsRequired(paymentMethod);
        });
    }

    private void markSellerConfigurationsAsRequired(PaymentMethod paymentMethod) {
        List<MerchantSellerPaymentMethod> sellerPaymentMethods =
                merchantSellerPaymentMethodRepository.findByPaymentMethod(paymentMethod);

        sellerPaymentMethods.forEach(sellerPaymentMethod -> sellerPaymentMethod.setConfigured(false));

        merchantSellerPaymentMethodRepository.saveAll(sellerPaymentMethods);

        merchantStatusService.refreshStatusesForSellerPaymentMethods(sellerPaymentMethods);
    }

    private String normalizePluginCode(String pluginCode) {
        return pluginCode.trim().toUpperCase();
    }

    private String normalizePaymentMethodCode(String paymentMethodCode) {
        return paymentMethodCode.trim().toUpperCase();
    }

}