package com.sep.psp.back.feature_plugin.service.impl;

import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.dto.PluginPaymentMethodRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginRegistrationResponse;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginRegistryService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PluginRegistryServiceImpl implements PluginRegistryService {

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public PluginRegistrationResponse registerPlugin(PluginRegistrationRequest request) {
        validateUniqueMethodCodes(request);

        PaymentPlugin plugin = paymentPluginRepository.findById(request.pluginCode())
                .orElseGet(PaymentPlugin::new);

        plugin.setCode(request.pluginCode());
        plugin.setDisplayName(request.displayName());
        plugin.setBaseUrl(request.baseUrl());
        plugin.setManifestPath(request.manifestPath());
        plugin.setHealthCheckPath(request.healthCheckPath());
        plugin.setActive(resolveActive(request.active()));

        PaymentPlugin savedPlugin = paymentPluginRepository.save(plugin);

        Set<String> registeredMethodCodes = new LinkedHashSet<>();

        for (PluginPaymentMethodRegistrationRequest methodRequest : request.methods()) {
            PaymentMethod paymentMethod = createOrUpdatePaymentMethod(
                    savedPlugin,
                    methodRequest
            );

            registeredMethodCodes.add(paymentMethod.getCode());

            if (Boolean.TRUE.equals(methodRequest.updateRequired())) {
                markSellerConfigurationsAsRequired(paymentMethod);
            }
        }

        deactivateMethodsMissingFromManifest(
                savedPlugin,
                registeredMethodCodes
        );

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_METHOD,
                LogStrings.Action.REGISTER_COMPLETED,
                "pluginCode={} methodCodes={}",
                savedPlugin.getCode(),
                registeredMethodCodes
        );

        return new PluginRegistrationResponse(
                savedPlugin.getCode(),
                registeredMethodCodes.stream().toList(),
                "Plugin registered successfully."
        );
    }

    private void validateUniqueMethodCodes(PluginRegistrationRequest request) {
        Set<String> uniqueMethodCodes = new LinkedHashSet<>();

        for (PluginPaymentMethodRegistrationRequest methodRequest : request.methods()) {
            if (!uniqueMethodCodes.add(methodRequest.code())) {
                throw new BadRequestException("Duplicate payment method code in plugin manifest.");
            }
        }
    }

    private PaymentMethod createOrUpdatePaymentMethod(
            PaymentPlugin plugin,
            PluginPaymentMethodRegistrationRequest methodRequest
    ) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodRequest.code())
                .orElseGet(PaymentMethod::new);

        if (paymentMethod.getPlugin() != null
                && !paymentMethod.getPlugin().getCode().equals(plugin.getCode())) {
            throw new BadRequestException("Payment method code is already registered by another plugin.");
        }

        paymentMethod.setCode(methodRequest.code());
        paymentMethod.setDisplayName(methodRequest.displayName());
        paymentMethod.setActive(resolveActive(methodRequest.active()));
        paymentMethod.setPlugin(plugin);
        paymentMethod.setConfigSchemaJson(methodRequest.configSchemaJson());

        return paymentMethodRepository.save(paymentMethod);
    }

    private void deactivateMethodsMissingFromManifest(
            PaymentPlugin plugin,
            Set<String> registeredMethodCodes
    ) {
        List<PaymentMethod> existingPluginMethods = paymentMethodRepository.findByPlugin(plugin);

        List<PaymentMethod> removedMethods = existingPluginMethods.stream()
                .filter(paymentMethod -> !registeredMethodCodes.contains(paymentMethod.getCode()))
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

        updateAffectedSellerAndMerchantStatuses(sellerPaymentMethods);
    }

    private void updateAffectedSellerAndMerchantStatuses(
            List<MerchantSellerPaymentMethod> sellerPaymentMethods
    ) {
        Map<String, MerchantSellerAccount> affectedSellers = new LinkedHashMap<>();

        sellerPaymentMethods.forEach(sellerPaymentMethod -> affectedSellers.put(
                sellerPaymentMethod.getSellerAccount().getId(),
                sellerPaymentMethod.getSellerAccount()
        ));

        affectedSellers.values().forEach(sellerAccount -> {
            boolean sellerActive = merchantSellerPaymentMethodRepository.findBySellerAccount(sellerAccount)
                    .stream()
                    .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);

            sellerAccount.setActive(sellerActive);

            merchantSellerAccountRepository.save(sellerAccount);
        });

        Map<String, Merchant> affectedMerchants = new LinkedHashMap<>();

        affectedSellers.values().forEach(sellerAccount -> affectedMerchants.put(
                sellerAccount.getMerchant().getMerchantId(),
                sellerAccount.getMerchant()
        ));

        affectedMerchants.values().forEach(this::updateMerchantActiveStatus);
    }

    private void updateMerchantActiveStatus(Merchant merchant) {
        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(merchant);

        boolean merchantActive = sellerAccounts.stream()
                .anyMatch(MerchantSellerAccount::isActive);

        merchant.setActive(merchantActive);

        merchantRepository.save(merchant);
    }

    private boolean resolveActive(Boolean active) {
        return active == null || active;
    }
}