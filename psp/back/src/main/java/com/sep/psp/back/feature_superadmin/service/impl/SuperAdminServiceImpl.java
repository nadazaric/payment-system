package com.sep.psp.back.feature_superadmin.service.impl;

import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginSecretEncryptionService;
import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginRequest;
import com.sep.psp.back.feature_superadmin.dto.CreateExpectedPluginResponse;
import com.sep.psp.back.feature_superadmin.dto.SuperAdminPluginResponse;
import com.sep.psp.back.feature_superadmin.service.interf.SuperAdminService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import com.sep.psp.back.shared.service.interf.ApiKeyGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_superadmin.dto.UpdatePluginStatusRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    PaymentPluginRepository paymentPluginRepository;

    @Autowired
    PluginSecretEncryptionService pluginSecretEncryptionService;

    @Autowired
    ApiKeyGeneratorService apiKeyGeneratorService;

    @Autowired
    AppLoggerService appLoggerService;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Value("${app.plugin-secret.prefix}")
    private String pluginSecretPrefix;

    @Value("${app.plugin-secret.alphabet}")
    private String pluginSecretAlphabet;

    @Value("${app.plugin-secret.length}")
    private int pluginSecretLength;

    @Override
    @Transactional
    public CreateExpectedPluginResponse createExpectedPlugin(CreateExpectedPluginRequest request) {
        String pluginCode = normalizePluginCode(request.pluginCode());

        if (paymentPluginRepository.existsById(pluginCode)) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_PLUGIN,
                    LogStrings.Action.EXPECTED_PLUGIN_CREATE_REJECTED,
                    "reason={} pluginCode={}",
                    LogStrings.Reason.PAYMENT_PLUGIN_EXISTS,
                    pluginCode
            );

            throw new BadRequestException("Payment plugin already exists.");
        }

        String pluginSecret = apiKeyGeneratorService.generateApiKey(
                pluginSecretPrefix,
                pluginSecretAlphabet,
                pluginSecretLength
        );
        String encryptedPluginSecret = pluginSecretEncryptionService.encrypt(pluginSecret);

        PaymentPlugin paymentPlugin = new PaymentPlugin(
                pluginCode,
                request.displayName().trim(),
                encryptedPluginSecret
        );

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_PLUGIN,
                LogStrings.Action.EXPECTED_PLUGIN_CREATED,
                "pluginCode={} displayName={} activeByAdmin={} active={}",
                savedPlugin.getCode(),
                savedPlugin.getDisplayName(),
                savedPlugin.isActiveByAdmin(),
                savedPlugin.isActive()
        );

        return new CreateExpectedPluginResponse(
                savedPlugin.getCode(),
                savedPlugin.getDisplayName(),
                pluginSecret,
                savedPlugin.isActiveByAdmin(),
                savedPlugin.isActive(),
                "Expected plugin created successfully."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SuperAdminPluginResponse> getPlugins() {
        List<PaymentPlugin> paymentPlugins = paymentPluginRepository.findAll();

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_PLUGIN,
                LogStrings.Action.PAYMENT_PLUGINS_LISTED,
                "count={}",
                paymentPlugins.size()
        );

        return paymentPlugins.stream()
                .map(this::toSuperAdminPluginResponse)
                .toList();
    }

    private String normalizePluginCode(String pluginCode) {
        return pluginCode.trim().toUpperCase();
    }

    private SuperAdminPluginResponse toSuperAdminPluginResponse(PaymentPlugin paymentPlugin) {
        return new SuperAdminPluginResponse(
                paymentPlugin.getCode(),
                paymentPlugin.getDisplayName(),
                paymentPlugin.getBaseUrl(),
                paymentPlugin.isActiveByAdmin(),
                paymentPlugin.isActive()
        );
    }

    @Override
    @Transactional
    public SuperAdminPluginResponse updatePluginStatus(UpdatePluginStatusRequest request) {
        String normalizedPluginCode = normalizePluginCode(request.pluginCode());

        PaymentPlugin paymentPlugin = paymentPluginRepository.findById(normalizedPluginCode)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT_PLUGIN,
                            LogStrings.Action.PLUGIN_STATUS_UPDATE_REJECTED,
                            "reason={} pluginCode={}",
                            LogStrings.Reason.PAYMENT_PLUGIN_NOT_FOUND,
                            normalizedPluginCode
                    );

                    return new BadRequestException("Payment plugin does not exist.");
                });

        paymentPlugin.setActiveByAdmin(request.activeByAdmin());
        paymentPlugin.setActive(false);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(paymentPlugin);

        updateAffectedSellerAndMerchantStatuses(savedPlugin);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_PLUGIN,
                LogStrings.Action.PLUGIN_STATUS_UPDATED,
                "pluginCode={} activeByAdmin={} active={}",
                savedPlugin.getCode(),
                savedPlugin.isActiveByAdmin(),
                savedPlugin.isActive()
        );

        return toSuperAdminPluginResponse(savedPlugin);
    }

    private void updateAffectedSellerAndMerchantStatuses(PaymentPlugin paymentPlugin) {
        List<PaymentMethod> pluginPaymentMethods = paymentMethodRepository.findByPlugin(paymentPlugin);

        Map<String, MerchantSellerAccount> affectedSellers = new LinkedHashMap<>();

        pluginPaymentMethods.forEach(paymentMethod -> {
            List<MerchantSellerPaymentMethod> sellerPaymentMethods =
                    merchantSellerPaymentMethodRepository.findByPaymentMethod(paymentMethod);

            sellerPaymentMethods.forEach(sellerPaymentMethod -> affectedSellers.put(
                    sellerPaymentMethod.getSellerAccount().getId(),
                    sellerPaymentMethod.getSellerAccount()
            ));
        });

        affectedSellers.values().forEach(this::updateSellerActiveStatus);

        Map<String, Merchant> affectedMerchants = new LinkedHashMap<>();

        affectedSellers.values().forEach(sellerAccount -> affectedMerchants.put(
                sellerAccount.getMerchant().getMerchantId(),
                sellerAccount.getMerchant()
        ));

        affectedMerchants.values().forEach(this::updateMerchantActiveStatus);
    }

    private void updateSellerActiveStatus(MerchantSellerAccount sellerAccount) {
        boolean sellerActive = merchantSellerPaymentMethodRepository.findBySellerAccount(sellerAccount)
                .stream()
                .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);

        sellerAccount.setActive(sellerActive);

        merchantSellerAccountRepository.save(sellerAccount);
    }

    private void updateMerchantActiveStatus(Merchant merchant) {
        List<MerchantSellerAccount> sellerAccounts = merchantSellerAccountRepository.findByMerchant(merchant);

        boolean merchantActive = sellerAccounts.stream()
                .anyMatch(MerchantSellerAccount::isActive);

        merchant.setActive(merchantActive);

        merchantRepository.save(merchant);
    }

}