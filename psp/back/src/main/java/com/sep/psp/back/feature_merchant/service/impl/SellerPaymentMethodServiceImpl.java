package com.sep.psp.back.feature_merchant.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodRequest;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodResponse;
import com.sep.psp.back.feature_merchant.dto.UpdateSellerPaymentMethodsRequest;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantAdminRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantStatusService;
import com.sep.psp.back.feature_merchant.service.interf.SellerPaymentMethodService;
import com.sep.psp.back.feature_payment.dto.PaymentMethodConfigField;
import com.sep.psp.back.feature_payment.model.PaymentMethod;
import com.sep.psp.back.feature_payment.repository.PaymentMethodRepository;
import com.sep.psp.back.feature_plugin.dto.PluginConfigurationResponse;
import com.sep.psp.back.feature_plugin.service.interf.PluginConfigurationService;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SellerPaymentMethodServiceImpl implements SellerPaymentMethodService {

    @Autowired
    MerchantAdminRepository merchantAdminRepository;

    @Autowired
    MerchantSellerAccountRepository merchantSellerAccountRepository;

    @Autowired
    MerchantSellerPaymentMethodRepository merchantSellerPaymentMethodRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    @Autowired
    PluginConfigurationService pluginConfigurationService;

    @Autowired
    MerchantStatusService merchantStatusService;

    @Autowired
    AppLoggerService appLoggerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void updateSellerPaymentMethods(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request,
            String authenticatedUsername
    ) {
        validatePaymentMethodSelection(
                sellerId,
                request
        );

        MerchantAdmin merchantAdmin = getAuthenticatedMerchantAdmin(authenticatedUsername);
        Merchant merchant = merchantAdmin.getMerchant();

        MerchantSellerAccount sellerAccount = getSellerAccountForMerchant(
                sellerId,
                merchant
        );

        Set<String> requestedPaymentMethodCodes = getUniquePaymentMethodCodes(request);

        List<PaymentMethod> paymentMethods = getPaymentMethodsOrThrow(
                requestedPaymentMethodCodes,
                merchant,
                sellerId
        );

        validatePaymentMethodsAreActive(
                paymentMethods,
                merchant,
                sellerId,
                requestedPaymentMethodCodes
        );

        validatePaymentPluginsAreActive(
                paymentMethods,
                merchant,
                sellerId,
                requestedPaymentMethodCodes
        );

        removeUnselectedSellerPaymentMethods(
                sellerAccount,
                requestedPaymentMethodCodes
        );

        validateSelectedPaymentMethodsAreConfigured(
                sellerAccount,
                paymentMethods
        );

        merchantStatusService.refreshSellerAndMerchantStatus(sellerAccount);

        logPaymentMethodsUpdated(
                merchant,
                sellerAccount,
                requestedPaymentMethodCodes
        );
    }

    @Override
    @Transactional
    public ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            ConfigureSellerPaymentMethodRequest request,
            String authenticatedUsername
    ) {
        MerchantAdmin merchantAdmin = getAuthenticatedMerchantAdmin(authenticatedUsername);
        Merchant merchant = merchantAdmin.getMerchant();

        MerchantSellerAccount sellerAccount = getSellerAccountForMerchant(
                sellerId,
                merchant
        );

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodCode)
                .orElseThrow(() -> new BadRequestException("Payment method does not exist."));

        validateSinglePaymentMethodIsActive(paymentMethod);
        validateSinglePaymentPluginIsActive(paymentMethod);

        validateConfigurationValues(
                paymentMethod,
                request
        );

        PluginConfigurationResponse pluginResponse = pluginConfigurationService.configurePaymentMethod(
                paymentMethod,
                merchant,
                sellerAccount,
                request.values()
        );

        if (!pluginResponse.configured()) {
            throw new BadRequestException("Payment plugin rejected seller payment method configuration.");
        }

        MerchantSellerPaymentMethod sellerPaymentMethod = merchantSellerPaymentMethodRepository
                .findBySellerAccountAndPaymentMethod(
                        sellerAccount,
                        paymentMethod
                )
                .orElseGet(() -> new MerchantSellerPaymentMethod(
                        sellerAccount,
                        paymentMethod,
                        false
                ));

        sellerPaymentMethod.setConfigured(true);

        merchantSellerPaymentMethodRepository.save(sellerPaymentMethod);

        merchantStatusService.refreshSellerAndMerchantStatus(sellerAccount);

        return new ConfigureSellerPaymentMethodResponse(
                paymentMethod.getCode(),
                true,
                pluginResponse.message()
        );
    }

    private void validatePaymentMethodSelection(
            String sellerId,
            UpdateSellerPaymentMethodsRequest request
    ) {
        if (request.paymentMethodCodes() == null || request.paymentMethodCodes().isEmpty()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_METHOD,
                    LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                    "reason={} sellerId={}",
                    LogStrings.Reason.EMPTY_SELECTION,
                    sellerId
            );

            throw new BadRequestException("At least one payment method must be selected.");
        }
    }

    private MerchantAdmin getAuthenticatedMerchantAdmin(String authenticatedUsername) {
        return merchantAdminRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new BadRequestException("Authenticated merchant admin not found."));
    }

    private MerchantSellerAccount getSellerAccountForMerchant(
            String sellerId,
            Merchant merchant
    ) {
        MerchantSellerAccount sellerAccount = merchantSellerAccountRepository.findById(sellerId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT_METHOD,
                            LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                            "reason={} merchantId={} sellerId={}",
                            LogStrings.Reason.SELLER_NOT_FOUND,
                            merchant.getMerchantId(),
                            sellerId
                    );

                    return new BadRequestException("Seller account not found.");
                });

        if (!sellerAccount.getMerchant().getMerchantId().equals(merchant.getMerchantId())) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_METHOD,
                    LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={}",
                    LogStrings.Reason.OWNER_MISMATCH,
                    merchant.getMerchantId(),
                    sellerId
            );

            throw new BadRequestException("Seller account does not belong to the authenticated merchant.");
        }

        return sellerAccount;
    }

    private Set<String> getUniquePaymentMethodCodes(UpdateSellerPaymentMethodsRequest request) {
        return new LinkedHashSet<>(request.paymentMethodCodes());
    }

    private List<PaymentMethod> getPaymentMethodsOrThrow(
            Set<String> requestedPaymentMethodCodes,
            Merchant merchant,
            String sellerId
    ) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findAllById(requestedPaymentMethodCodes);

        if (paymentMethods.size() != requestedPaymentMethodCodes.size()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_METHOD,
                    LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={} requestedCodes={}",
                    LogStrings.Reason.UNKNOWN_PAYMENT_METHOD,
                    merchant.getMerchantId(),
                    sellerId,
                    requestedPaymentMethodCodes
            );

            throw new BadRequestException("One or more payment methods do not exist.");
        }

        return paymentMethods;
    }

    private void validatePaymentMethodsAreActive(
            List<PaymentMethod> paymentMethods,
            Merchant merchant,
            String sellerId,
            Set<String> requestedPaymentMethodCodes
    ) {
        boolean hasInactivePaymentMethod = paymentMethods.stream()
                .anyMatch(paymentMethod -> !paymentMethod.isActive());

        if (hasInactivePaymentMethod) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_METHOD,
                    LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={} requestedCodes={}",
                    LogStrings.Reason.INACTIVE_PAYMENT_METHOD,
                    merchant.getMerchantId(),
                    sellerId,
                    requestedPaymentMethodCodes
            );

            throw new BadRequestException("One or more payment methods are not active.");
        }
    }

    private void validatePaymentPluginsAreActive(
            List<PaymentMethod> paymentMethods,
            Merchant merchant,
            String sellerId,
            Set<String> requestedPaymentMethodCodes
    ) {
        boolean hasInactivePaymentPlugin = paymentMethods.stream()
                .anyMatch(paymentMethod -> !paymentMethod.getPlugin().isActive());

        if (hasInactivePaymentPlugin) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT_METHOD,
                    LogStrings.Action.PAYMENT_METHOD_UPDATE_REJECTED,
                    "reason={} merchantId={} sellerId={} requestedCodes={}",
                    LogStrings.Reason.INACTIVE_PAYMENT_PLUGIN,
                    merchant.getMerchantId(),
                    sellerId,
                    requestedPaymentMethodCodes
            );

            throw new BadRequestException("One or more payment method plugins are not active.");
        }
    }

    private void removeUnselectedSellerPaymentMethods(
            MerchantSellerAccount sellerAccount,
            Set<String> requestedPaymentMethodCodes
    ) {
        List<MerchantSellerPaymentMethod> existingSellerPaymentMethods =
                merchantSellerPaymentMethodRepository.findBySellerAccount(sellerAccount);

        existingSellerPaymentMethods.stream()
                .filter(sellerPaymentMethod -> !requestedPaymentMethodCodes.contains(
                        sellerPaymentMethod.getPaymentMethod().getCode()
                ))
                .forEach(merchantSellerPaymentMethodRepository::delete);
    }

    private void validateSelectedPaymentMethodsAreConfigured(
            MerchantSellerAccount sellerAccount,
            List<PaymentMethod> paymentMethods
    ) {
        for (PaymentMethod paymentMethod : paymentMethods) {
            MerchantSellerPaymentMethod sellerPaymentMethod = merchantSellerPaymentMethodRepository
                    .findBySellerAccountAndPaymentMethod(
                            sellerAccount,
                            paymentMethod
                    )
                    .orElseThrow(() -> new BadRequestException(
                            "Payment method must be configured before it can be assigned to the seller."
                    ));

            if (!sellerPaymentMethod.isConfigured()) {
                throw new BadRequestException(
                        "Payment method must be configured before it can be assigned to the seller."
                );
            }
        }
    }

    private void validateSinglePaymentMethodIsActive(PaymentMethod paymentMethod) {
        if (!paymentMethod.isActive()) {
            throw new BadRequestException("Payment method is not active.");
        }
    }

    private void validateSinglePaymentPluginIsActive(PaymentMethod paymentMethod) {
        if (!paymentMethod.getPlugin().isActive()) {
            throw new BadRequestException("Payment method plugin is not active.");
        }
    }

    private void validateConfigurationValues(
            PaymentMethod paymentMethod,
            ConfigureSellerPaymentMethodRequest request
    ) {
        List<PaymentMethodConfigField> configFields = readConfigFields(paymentMethod);

        Set<String> requiredFieldNames = configFields.stream()
                .map(PaymentMethodConfigField::fieldName)
                .collect(Collectors.toSet());

        for (String fieldName : requiredFieldNames) {
            String value = request.values().get(fieldName);

            if (value == null || value.isBlank()) {
                throw new BadRequestException("Missing configuration value: " + fieldName);
            }
        }

        Set<String> submittedFieldNames = new HashSet<>(request.values().keySet());

        submittedFieldNames.removeAll(requiredFieldNames);

        if (!submittedFieldNames.isEmpty()) {
            throw new BadRequestException("Unknown configuration fields: " + submittedFieldNames);
        }
    }

    private List<PaymentMethodConfigField> readConfigFields(PaymentMethod paymentMethod) {
        try {
            return objectMapper.readValue(
                    paymentMethod.getConfigSchemaJson(),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception exception) {
            throw new BadRequestException("Invalid payment method configuration schema.");
        }
    }

    private void logPaymentMethodsUpdated(
            Merchant merchant,
            MerchantSellerAccount sellerAccount,
            Set<String> requestedPaymentMethodCodes
    ) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT_METHOD,
                LogStrings.Action.PAYMENT_METHODS_UPDATED,
                "merchantId={} sellerId={} codes={}",
                merchant.getMerchantId(),
                sellerAccount.getId(),
                requestedPaymentMethodCodes
        );
    }

}