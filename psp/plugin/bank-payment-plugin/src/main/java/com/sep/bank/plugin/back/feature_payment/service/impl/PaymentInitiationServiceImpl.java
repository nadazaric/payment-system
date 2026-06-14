package com.sep.bank.plugin.back.feature_payment.service.impl;

import com.sep.bank.plugin.back.feature_payment.client.BankClient;
import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.CreateBankPaymentResponse;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationRequest;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PluginPaymentInitiationResponse;
import com.sep.bank.plugin.back.feature_payment.enumeration.PaymentResultDeliveryStatus;
import com.sep.bank.plugin.back.feature_payment.enumeration.PluginPaymentStatus;
import com.sep.bank.plugin.back.feature_payment.model.PluginPayment;
import com.sep.bank.plugin.back.feature_payment.repository.PluginPaymentRepository;
import com.sep.bank.plugin.back.feature_payment.service.interf.PaymentInitiationService;
import com.sep.bank.plugin.back.feature_psp.model.BankPluginSellerConfiguration;
import com.sep.bank.plugin.back.feature_psp.repository.BankPluginSellerConfigurationRepository;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentInitiationServiceImpl implements PaymentInitiationService {

    @Autowired
    BankPluginSellerConfigurationRepository bankPluginSellerConfigurationRepository;

    @Autowired
    PluginPaymentRepository pluginPaymentRepository;

    @Autowired
    BankClient bankClient;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.plugin.bank-callback-url}")
    String bankCallbackUrl;

    @Override
    @Transactional
    public PluginPaymentInitiationResponse initiatePayment(PluginPaymentInitiationRequest request) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_INITIATE_REQUEST_RECEIVED,
                "paymentId={} merchantId={} sellerReference={} paymentMethodCode={} amount={} currency={}",
                request.paymentId(),
                request.merchantId(),
                request.sellerReference(),
                request.paymentMethodCode(),
                request.amount(),
                request.currency()
        );

        PluginPayment existingPayment = pluginPaymentRepository.findByPspPaymentId(request.paymentId())
                .orElse(null);

        if (existingPayment != null) {
            appLoggerService.info(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_INITIATE_ALREADY_EXISTS,
                    "paymentId={} bankPaymentId={}",
                    existingPayment.getPspPaymentId(),
                    existingPayment.getBankPaymentId()
            );

            return new PluginPaymentInitiationResponse(
                    existingPayment.getBankPaymentUrl()
            );
        }

        BankPluginSellerConfiguration configuration = bankPluginSellerConfigurationRepository
                .findByMerchantIdAndSellerReferenceAndPaymentMethodCode(
                        request.merchantId(),
                        request.sellerReference(),
                        request.paymentMethodCode()
                )
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_INITIATE_REJECTED,
                            "reason={} paymentId={} merchantId={} sellerReference={} paymentMethodCode={}",
                            LogStrings.Reason.SELLER_PAYMENT_CONFIGURATION_NOT_FOUND,
                            request.paymentId(),
                            request.merchantId(),
                            request.sellerReference(),
                            request.paymentMethodCode()
                    );

                    return new IllegalArgumentException("Seller payment method configuration not found.");
                });

        String stan = generateStan();
        LocalDateTime pspTimestamp = LocalDateTime.now();

        CreateBankPaymentResponse bankResponse = createBankPayment(
                request,
                configuration,
                stan,
                pspTimestamp
        );

        PluginPayment pluginPayment = new PluginPayment();

        pluginPayment.setPspPaymentId(request.paymentId());
        pluginPayment.setBankPaymentId(bankResponse.bankPaymentId());
        pluginPayment.setBankPaymentUrl(bankResponse.paymentUrl());
        pluginPayment.setMerchantId(request.merchantId());
        pluginPayment.setSellerReference(request.sellerReference());
        pluginPayment.setPaymentMethodCode(request.paymentMethodCode());
        pluginPayment.setBankMerchantId(configuration.getBankMerchantId());
        pluginPayment.setStan(stan);
        pluginPayment.setPspTimestamp(pspTimestamp);
        pluginPayment.setAmount(request.amount());
        pluginPayment.setCurrency(request.currency());
        pluginPayment.setStatus(PluginPaymentStatus.INITIATED);
        pluginPayment.setResultDeliveryStatus(PaymentResultDeliveryStatus.WAITING_BANK_RESULT);

        PluginPayment savedPluginPayment = pluginPaymentRepository.save(pluginPayment);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_INITIATE_COMPLETED,
                "paymentId={} bankPaymentId={} stan={} pspTimestamp={}",
                savedPluginPayment.getPspPaymentId(),
                savedPluginPayment.getBankPaymentId(),
                savedPluginPayment.getStan(),
                savedPluginPayment.getPspTimestamp()
        );

        return new PluginPaymentInitiationResponse(savedPluginPayment.getBankPaymentUrl());
    }

    private CreateBankPaymentResponse createBankPayment(
            PluginPaymentInitiationRequest request,
            BankPluginSellerConfiguration configuration,
            String stan,
            LocalDateTime pspTimestamp
    ) {
        try {
            CreateBankPaymentRequest bankRequest = new CreateBankPaymentRequest(
                    configuration.getBankMerchantId(),
                    stan,
                    pspTimestamp,
                    request.paymentMethodCode(),
                    request.amount(),
                    request.currency(),
                    request.successUrl(),
                    request.failUrl(),
                    request.errorUrl(),
                    bankCallbackUrl
            );

            CreateBankPaymentResponse bankResponse = bankClient.createPayment(bankRequest);

            if (bankResponse == null || bankResponse.paymentUrl() == null || bankResponse.paymentUrl().isBlank()) {
                throw new IllegalArgumentException("Bank payment response is not valid.");
            }

            return bankResponse;
        } catch (RestClientException | IllegalArgumentException exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.BANK_PAYMENT_INITIATE_REJECTED,
                    "reason={} paymentId={} merchantId={} sellerReference={} paymentMethodCode={} error={}",
                    LogStrings.Reason.BANK_PAYMENT_CREATE_FAILED,
                    request.paymentId(),
                    request.merchantId(),
                    request.sellerReference(),
                    request.paymentMethodCode(),
                    exception.getMessage()
            );

            throw exception;
        }
    }

    private String generateStan() {
        return String.format(
                "%06d",
                ThreadLocalRandom.current()
                        .nextInt(0, 1_000_000)
        );
    }

}