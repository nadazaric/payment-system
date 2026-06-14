package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.client.PaymentPluginClient;
import com.sep.bank.back.feature_payment.dto.BankPaymentCallbackRequest;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.service.interf.PaymentCallbackService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentCallbackServiceImpl implements PaymentCallbackService {

    @Autowired
    PaymentPluginClient paymentPluginClient;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.plugin.callback.max-attempts:3}")
    int maxCallbackAttempts;

    @Value("${app.plugin.callback.retry-delay-ms:1000}")
    long callbackRetryDelayMs;

    @Override
    public void sendPaymentResultCallback(Payment payment, String message) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxCallbackAttempts; attempt++) {
            try {
                sendPaymentResultCallbackOnce(payment, message);

                return;
            } catch (Exception exception) {
                lastException = exception;

                if (attempt == maxCallbackAttempts) {
                    break;
                }

                appLoggerService.warn(
                        LogStrings.Feature.PAYMENT,
                        LogStrings.Action.PLUGIN_CALLBACK_RETRY,
                        "bankPaymentId={} attempt={} maxAttempts={} error={}",
                        payment.getId(),
                        attempt,
                        maxCallbackAttempts,
                        exception.getMessage()
                );

                sleepBeforeRetry();
            }
        }

        appLoggerService.error(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PLUGIN_CALLBACK_FAILED,
                "reason={} bankPaymentId={} attempts={} error={}",
                LogStrings.Reason.PLUGIN_CALLBACK_FAILED,
                payment.getId(),
                maxCallbackAttempts,
                lastException == null ? "unknown" : lastException.getMessage()
        );
    }

    private void sendPaymentResultCallbackOnce(Payment payment, String message) {
        BankPaymentCallbackRequest request = new BankPaymentCallbackRequest(
                payment.getId().toString(),
                payment.getStan(),
                mapStatusForPlugin(payment.getStatus()),
                payment.getGlobalTransactionId(),
                payment.getAcquirerTimestamp(),
                message
        );

        paymentPluginClient.sendPaymentCallback(
                payment.getPluginCallbackUrl(),
                request
        );
    }

    private PaymentStatus mapStatusForPlugin(PaymentStatus paymentStatus) {
        if (PaymentStatus.EXPIRED.equals(paymentStatus)) {
            return PaymentStatus.FAILED;
        }

        return paymentStatus;
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(callbackRetryDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException("Plugin callback retry interrupted.");
        }
    }

}