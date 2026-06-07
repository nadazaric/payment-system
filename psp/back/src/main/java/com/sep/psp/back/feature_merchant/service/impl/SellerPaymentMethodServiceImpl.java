package com.sep.psp.back.feature_merchant.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodRequest;
import com.sep.psp.back.feature_merchant.dto.ConfigureSellerPaymentMethodResponse;
import com.sep.psp.back.feature_merchant.model.Merchant;
import com.sep.psp.back.feature_merchant.model.MerchantAdmin;
import com.sep.psp.back.feature_merchant.model.MerchantSellerAccount;
import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerAccountRepository;
import com.sep.psp.back.feature_merchant.repository.MerchantSellerPaymentMethodRepository;
import com.sep.psp.back.feature_merchant.service.interf.MerchantAdminContextService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SellerPaymentMethodServiceImpl implements SellerPaymentMethodService {

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
    MerchantAdminContextService merchantAdminContextService;

    @Autowired
    AppLoggerService appLoggerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ConfigureSellerPaymentMethodResponse configureSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            ConfigureSellerPaymentMethodRequest request,
            String authenticatedUsername
    ) {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getMerchantAdminByUsername(authenticatedUsername);
        Merchant merchant = merchantAdmin.getMerchant();

        MerchantSellerAccount sellerAccount = getSellerAccountForMerchant(
                sellerId,
                merchant
        );

        PaymentMethod paymentMethod = getPaymentMethodOrThrow(paymentMethodCode);

        if (!paymentMethod.isActive()) {
            throw new BadRequestException("Payment method is not active.");
        }

        if (!paymentMethod.getPlugin().isActive()) {
            throw new BadRequestException("Payment method plugin is not active.");
        }

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

        merchantSellerPaymentMethodRepository.saveAndFlush(sellerPaymentMethod);

        merchantStatusService.refreshSellerAndMerchantStatus(sellerAccount);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_METHOD,
                LogStrings.Action.PAYMENT_METHOD_CONFIGURED,
                "merchantId={} sellerId={} paymentMethodCode={}",
                merchant.getMerchantId(),
                sellerAccount.getId(),
                paymentMethod.getCode()
        );

        return new ConfigureSellerPaymentMethodResponse(
                paymentMethod.getCode(),
                true,
                pluginResponse.message()
        );
    }

    @Override
    @Transactional
    public void removeSellerPaymentMethod(
            String sellerId,
            String paymentMethodCode,
            String authenticatedUsername
    ) {
        MerchantAdmin merchantAdmin = merchantAdminContextService.getMerchantAdminByUsername(authenticatedUsername);
        Merchant merchant = merchantAdmin.getMerchant();

        MerchantSellerAccount sellerAccount = getSellerAccountForMerchant(
                sellerId,
                merchant
        );

        PaymentMethod paymentMethod = getPaymentMethodOrThrow(paymentMethodCode);

        MerchantSellerPaymentMethod sellerPaymentMethod = merchantSellerPaymentMethodRepository
                .findBySellerAccountAndPaymentMethod(
                        sellerAccount,
                        paymentMethod
                )
                .orElseThrow(() -> new BadRequestException("Seller payment method does not exist."));

        validateMerchantKeepsAtLeastOneAvailablePaymentMethod(
                merchant,
                sellerPaymentMethod
        );

        merchantSellerPaymentMethodRepository.delete(sellerPaymentMethod);
        merchantSellerPaymentMethodRepository.flush();

        merchantStatusService.refreshSellerAndMerchantStatus(sellerAccount);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT_METHOD,
                LogStrings.Action.PAYMENT_METHOD_REMOVED,
                "merchantId={} sellerId={} paymentMethodCode={}",
                merchant.getMerchantId(),
                sellerAccount.getId(),
                paymentMethod.getCode()
        );
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

    private PaymentMethod getPaymentMethodOrThrow(String paymentMethodCode) {
        return paymentMethodRepository.findById(paymentMethodCode)
                .orElseThrow(() -> new BadRequestException("Payment method does not exist."));
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

    private void validateMerchantKeepsAtLeastOneAvailablePaymentMethod(
            Merchant merchant,
            MerchantSellerPaymentMethod sellerPaymentMethodToRemove
    ) {
        if (!sellerPaymentMethodToRemove.isAvailableForPayments()) {
            return;
        }

        boolean hasAnotherAvailablePaymentMethod = merchantSellerPaymentMethodRepository.findBySellerAccount_Merchant(merchant)
                .stream()
                .filter(sellerPaymentMethod -> !sellerPaymentMethod.getId()
                        .equals(sellerPaymentMethodToRemove.getId()))
                .anyMatch(MerchantSellerPaymentMethod::isAvailableForPayments);

        if (!hasAnotherAvailablePaymentMethod) {
            throw new BadRequestException("Merchant must have at least one active payment method.");
        }
    }

}