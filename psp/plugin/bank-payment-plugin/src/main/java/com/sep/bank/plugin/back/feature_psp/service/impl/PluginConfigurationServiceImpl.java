package com.sep.bank.plugin.back.feature_psp.service.impl;

import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationRequest;
import com.sep.bank.plugin.back.feature_psp.dto.psp.PluginConfigurationResponse;
import com.sep.bank.plugin.back.feature_psp.model.BankPluginSellerConfiguration;
import com.sep.bank.plugin.back.feature_psp.repository.BankPluginSellerConfigurationRepository;
import com.sep.bank.plugin.back.feature_psp.service.interf.PluginConfigurationService;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PluginConfigurationServiceImpl implements PluginConfigurationService {

    private static final String BANK_MERCHANT_ID_FIELD = "bankMerchantId";

    private static final String CARD_METHOD_CODE = "CARD";
    private static final String QR_METHOD_CODE = "QR";

    @Autowired
    BankPluginSellerConfigurationRepository bankPluginSellerConfigurationRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public PluginConfigurationResponse configure(PluginConfigurationRequest request) {
        appLoggerService.info(
                LogStrings.Feature.PLUGIN_CONFIGURATION,
                LogStrings.Action.CONFIGURATION_REQUEST_RECEIVED,
                "merchantId={} sellerReference={} paymentMethodCode={}",
                request.merchantId(),
                request.sellerReference(),
                request.paymentMethodCode()
        );

        if (!isSupportedPaymentMethod(request.paymentMethodCode())) {
            appLoggerService.warn(
                    LogStrings.Feature.PLUGIN_CONFIGURATION,
                    LogStrings.Action.CONFIGURATION_REJECTED,
                    "reason={} merchantId={} sellerReference={} paymentMethodCode={}",
                    LogStrings.Reason.INVALID_PAYMENT_METHOD_CODE,
                    request.merchantId(),
                    request.sellerReference(),
                    request.paymentMethodCode()
            );

            return new PluginConfigurationResponse(
                    false,
                    "Unsupported payment method code."
            );
        }

        String bankMerchantId = request.values()
                .get(BANK_MERCHANT_ID_FIELD);

        if (bankMerchantId == null || bankMerchantId.isBlank()) {
            appLoggerService.warn(
                    LogStrings.Feature.PLUGIN_CONFIGURATION,
                    LogStrings.Action.CONFIGURATION_REJECTED,
                    "reason={} merchantId={} sellerReference={} paymentMethodCode={}",
                    LogStrings.Reason.MISSING_BANK_MERCHANT_ID,
                    request.merchantId(),
                    request.sellerReference(),
                    request.paymentMethodCode()
            );

            return new PluginConfigurationResponse(
                    false,
                    "Missing bankMerchantId configuration value."
            );
        }

        BankPluginSellerConfiguration configuration = bankPluginSellerConfigurationRepository
                .findByMerchantIdAndSellerReferenceAndPaymentMethodCode(
                        request.merchantId(),
                        request.sellerReference(),
                        request.paymentMethodCode()
                )
                .orElseGet(BankPluginSellerConfiguration::new);

        configuration.setMerchantId(request.merchantId());
        configuration.setSellerReference(request.sellerReference());
        configuration.setPaymentMethodCode(request.paymentMethodCode());
        configuration.setBankMerchantId(bankMerchantId);

        bankPluginSellerConfigurationRepository.save(configuration);

        appLoggerService.info(
                LogStrings.Feature.PLUGIN_CONFIGURATION,
                LogStrings.Action.CONFIGURATION_SAVED,
                "merchantId={} sellerReference={} paymentMethodCode={} bankMerchantId={}",
                request.merchantId(),
                request.sellerReference(),
                request.paymentMethodCode(),
                bankMerchantId
        );

        return new PluginConfigurationResponse(
                true,
                "Configuration saved successfully."
        );
    }

    private boolean isSupportedPaymentMethod(String paymentMethodCode) {
        return CARD_METHOD_CODE.equals(paymentMethodCode)
                || QR_METHOD_CODE.equals(paymentMethodCode);
    }

}
