package com.sep.psp.back.feature_plugin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.dto.PluginPaymentMethodRegistrationRequest;
import com.sep.psp.back.feature_plugin.dto.PluginSyncRequest;
import com.sep.psp.back.feature_plugin.dto.PluginSyncResponse;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.feature_plugin.repository.PaymentPluginRepository;
import com.sep.psp.back.feature_plugin.service.interf.PluginHmacService;
import com.sep.psp.back.feature_plugin.service.interf.PluginSyncService;
import com.sep.psp.back.feature_plugin.service.interf.PluginSecretEncryptionService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PluginSyncServiceImpl implements PluginSyncService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.plugin.encryption-max-timestamp-age-minutes:5}")
    long maxTimestampAgeMinutes;

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
    PluginHmacService pluginHmacService;

    @Autowired
    PluginSecretEncryptionService pluginSecretEncryptionService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public PluginSyncResponse syncPlugin(
            String pluginCodeHeader,
            String timestamp,
            String signature,
            String requestBody
    ) {
        PluginSyncRequest request = readRegistrationRequest(requestBody);

        String pluginCode = normalizePluginCode(request.pluginCode());

        validateSyncHeaders(
                pluginCodeHeader,
                pluginCode,
                timestamp,
                signature
        );

        PaymentPlugin plugin = paymentPluginRepository.findById(pluginCode)
                .orElseThrow(() -> new BadRequestException("Payment plugin is not expected by PSP."));

        if (!plugin.isActiveByAdmin()) {
            throw new BadRequestException("Payment plugin is disabled by PSP super admin.");
        }

        validateSignature(
                plugin,
                timestamp,
                requestBody,
                signature
        );

        validateUniqueMethodCodes(request);

        plugin.setDisplayName(request.displayName());
        plugin.setBaseUrl(request.baseUrl());
        plugin.setActive(true);

        PaymentPlugin savedPlugin = paymentPluginRepository.save(plugin);

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

    private PluginSyncRequest readRegistrationRequest(String requestBody) {
        try {
            return objectMapper.readValue(
                    requestBody,
                    PluginSyncRequest.class
            );
        } catch (Exception exception) {
            throw new BadRequestException("Invalid plugin sync request body.");
        }
    }

    private void validateSyncHeaders(
            String pluginCodeHeader,
            String pluginCode,
            String timestamp,
            String signature
    ) {
        if (pluginCodeHeader == null || pluginCodeHeader.isBlank()) {
            throw new BadRequestException("Plugin code header is required.");
        }

        if (!normalizePluginCode(pluginCodeHeader).equals(pluginCode)) {
            throw new BadRequestException("Plugin code header does not match request body.");
        }

        if (timestamp == null || timestamp.isBlank()) {
            throw new BadRequestException("Timestamp header is required.");
        }

        if (signature == null || signature.isBlank()) {
            throw new BadRequestException("Signature header is required.");
        }

        validateTimestamp(timestamp);
    }

    private void validateTimestamp(String timestamp) {
        try {
            Instant requestInstant = Instant.parse(timestamp);
            Instant now = Instant.now();

            Duration age = Duration.between(
                    requestInstant,
                    now
            ).abs();

            if (age.compareTo(Duration.ofMinutes(maxTimestampAgeMinutes)) > 0) {
                throw new BadRequestException("Plugin request timestamp is not valid.");
            }
        } catch (Exception exception) {
            throw new BadRequestException("Plugin request timestamp is not valid.");
        }
    }

    private void validateSignature(PaymentPlugin plugin, String timestamp, String requestBody, String signature) {
        String pluginSecret = pluginSecretEncryptionService.decrypt(plugin.getEncryptedPluginSecret());

        boolean signatureValid = pluginHmacService.isSignatureValid(pluginSecret, timestamp, requestBody, signature);

        if (!signatureValid) {
            throw new BadRequestException("Invalid plugin request signature.");
        }
    }

    private void validateUniqueMethodCodes(PluginSyncRequest request) {
        Set<String> uniqueMethodCodes = new LinkedHashSet<>();

        for (PluginPaymentMethodRegistrationRequest methodRequest : request.methods()) {
            if (!uniqueMethodCodes.add(methodRequest.code())) {
                throw new BadRequestException("Duplicate payment method code in plugin manifest.");
            }
        }
    }

    private PaymentMethod createOrUpdatePaymentMethod(PaymentPlugin plugin, PluginPaymentMethodRegistrationRequest methodRequest) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodRequest.code())
                .orElseGet(PaymentMethod::new);

        if (paymentMethod.getPlugin() != null
                && !paymentMethod.getPlugin().getCode().equals(plugin.getCode())) {
            throw new BadRequestException("Payment method code is already registered by another plugin.");
        }

        paymentMethod.setCode(methodRequest.code());
        paymentMethod.setDisplayName(methodRequest.displayName());
        paymentMethod.setActive(methodRequest.active());
        paymentMethod.setPlugin(plugin);
        paymentMethod.setConfigSchemaJson(methodRequest.configSchemaJson());

        return paymentMethodRepository.save(paymentMethod);
    }

    private void deactivateMethodsMissingFromManifest(PaymentPlugin plugin, Set<String> synchronizedMethodCodes) {
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

    private String normalizePluginCode(String pluginCode) {
        return pluginCode.trim().toUpperCase();
    }

}