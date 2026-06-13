package com.sep.bank.plugin.back.feature_payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackResponse;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PspPaymentCallbackRequest;
import com.sep.bank.plugin.back.feature_payment.enumeration.PaymentResultDeliveryStatus;
import com.sep.bank.plugin.back.feature_payment.model.PluginPayment;
import com.sep.bank.plugin.back.feature_payment.repository.PluginPaymentRepository;
import com.sep.bank.plugin.back.feature_payment.service.interf.BankPaymentCallbackService;
import com.sep.bank.plugin.back.feature_psp.client.PspClient;
import com.sep.bank.plugin.back.shared.logging.LogStrings;
import com.sep.bank.plugin.back.shared.logging.service.interf.AppLoggerService;
import com.sep.bank.plugin.back.shared.security.service.interf.HmacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BankPaymentCallbackServiceImpl implements BankPaymentCallbackService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PluginPaymentRepository pluginPaymentRepository;

    @Autowired
    PspClient pspClient;

    @Autowired
    HmacService hmacService;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.security.psp-secret}")
    String pspSecret;

    @Value("${app.security.plugin-code}")
    String pluginCode;

    @Value("${app.psp.callback.max-attempts:3}")
    int maxCallbackAttempts;

    @Value("${app.psp.callback.retry-delay-ms:1000}")
    long callbackRetryDelayMs;

    @Override
    @Transactional
    public BankPaymentCallbackResponse processBankCallback(
            BankPaymentCallbackRequest request
    ) {
        PluginPayment pluginPayment = findPluginPayment(request.bankPaymentId());

        validateStan(pluginPayment, request);
        updatePluginPaymentFromBankCallback(pluginPayment, request);
        boolean deliveredToPsp = sendCallbackToPspWithRetry(pluginPayment, request);

        if (deliveredToPsp) {
            markResultDeliveredToPsp(pluginPayment);
        }

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CALLBACK_COMPLETED,
                "paymentId={} bankPaymentId={} status={} resultDeliveryStatus={}",
                pluginPayment.getPspPaymentId(),
                pluginPayment.getBankPaymentId(),
                pluginPayment.getStatus(),
                pluginPayment.getResultDeliveryStatus()
        );

        return new BankPaymentCallbackResponse(
                pluginPayment.getBankPaymentId(),
                pluginPayment.getStatus(),
                pluginPayment.getResultDeliveryStatus()
        );
    }

    private PluginPayment findPluginPayment(String bankPaymentId) {
        return pluginPaymentRepository.findByBankPaymentId(bankPaymentId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_CALLBACK_REJECTED,
                            "reason={} bankPaymentId={}",
                            LogStrings.Reason.PLUGIN_PAYMENT_NOT_FOUND,
                            bankPaymentId
                    );

                    return new IllegalArgumentException("Plugin payment was not found.");
                });
    }

    private void validateStan(
            PluginPayment pluginPayment,
            BankPaymentCallbackRequest request
    ) {
        if (pluginPayment.getStan().equals(request.stan())) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CALLBACK_REJECTED,
                "reason={} paymentId={} bankPaymentId={} expectedStan={} receivedStan={}",
                LogStrings.Reason.STAN_MISMATCH,
                pluginPayment.getPspPaymentId(),
                pluginPayment.getBankPaymentId(),
                pluginPayment.getStan(),
                request.stan()
        );

        throw new IllegalArgumentException("Bank callback STAN does not match payment.");
    }

    private void updatePluginPaymentFromBankCallback(
            PluginPayment pluginPayment,
            BankPaymentCallbackRequest request
    ) {
        pluginPayment.setStatus(request.status());
        pluginPayment.setGlobalTransactionId(request.globalTransactionId());
        pluginPayment.setAcquirerTimestamp(request.acquirerTimestamp());
        pluginPayment.setResultMessage(request.message());
        pluginPayment.setResultDeliveryStatus(PaymentResultDeliveryStatus.RECEIVED_FROM_BANK);

        pluginPaymentRepository.save(pluginPayment);
    }

    private boolean sendCallbackToPspWithRetry(
            PluginPayment pluginPayment,
            BankPaymentCallbackRequest bankRequest
    ) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxCallbackAttempts; attempt++) {
            try {
                sendCallbackToPsp(
                        pluginPayment,
                        bankRequest
                );

                return true;
            } catch (Exception exception) {
                lastException = exception;

                if (attempt == maxCallbackAttempts) {
                    break;
                }

                appLoggerService.warn(
                        LogStrings.Feature.PAYMENT,
                        LogStrings.Action.PSP_CALLBACK_RETRY,
                        "paymentId={} bankPaymentId={} attempt={} maxAttempts={} error={}",
                        pluginPayment.getPspPaymentId(),
                        pluginPayment.getBankPaymentId(),
                        attempt,
                        maxCallbackAttempts,
                        exception.getMessage()
                );

                sleepBeforeRetry();
            }
        }

        appLoggerService.error(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PSP_CALLBACK_FAILED,
                "reason={} paymentId={} bankPaymentId={} attempts={} error={}",
                LogStrings.Reason.PSP_CALLBACK_FAILED,
                pluginPayment.getPspPaymentId(),
                pluginPayment.getBankPaymentId(),
                maxCallbackAttempts,
                lastException == null ? "unknown" : lastException.getMessage()
        );

        return false;
    }

    private void sendCallbackToPsp(
            PluginPayment pluginPayment,
            BankPaymentCallbackRequest bankRequest
    ) {
        PspPaymentCallbackRequest pspRequest = new PspPaymentCallbackRequest(
                bankRequest.status(),
                bankRequest.message(),
                bankRequest.stan(),
                bankRequest.globalTransactionId(),
                bankRequest.acquirerTimestamp()
        );

        String requestBody = writeJson(pspRequest);
        String timestamp = Instant.now().toString();

        String signature = hmacService.generateSignature(
                pspSecret,
                timestamp,
                requestBody
        );

        pspClient.sendPaymentCallback(
                pluginPayment.getPspPaymentId(),
                pluginCode,
                timestamp,
                signature,
                requestBody
        );
    }

    private void markResultDeliveredToPsp(PluginPayment pluginPayment) {
        pluginPayment.setResultDeliveryStatus(PaymentResultDeliveryStatus.DELIVERED_TO_PSP);

        pluginPaymentRepository.save(pluginPayment);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            throw new IllegalStateException(LogStrings.Reason.JSON_SERIALIZATION_FAILED);
        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(callbackRetryDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException("PSP callback retry interrupted.");
        }
    }

}