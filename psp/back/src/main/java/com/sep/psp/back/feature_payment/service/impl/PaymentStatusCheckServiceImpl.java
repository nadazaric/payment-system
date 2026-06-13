package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginStatusCheckRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginStatusCheckResponse;
import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.repository.PaymentTransactionRepository;
import com.sep.psp.back.feature_payment.service.interf.PaymentPluginCallbackService;
import com.sep.psp.back.feature_payment.service.interf.PaymentStatusCheckService;
import com.sep.psp.back.feature_plugin.client.interf.PluginHttpClient;
import com.sep.psp.back.feature_plugin.model.PaymentPlugin;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentStatusCheckServiceImpl implements PaymentStatusCheckService {

    private static final String PAYMENT_STATUS_CHECK_ENDPOINT = "/api/plugin/payments/status-check";

    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    PluginHttpClient pluginHttpClient;

    @Autowired
    PaymentPluginCallbackService paymentPluginCallbackService;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.payment.status-check-min-age-seconds:30}")
    long statusCheckMinAgeSeconds;

    @Override
    @Transactional
    public void checkInitiatedPayments() {
        LocalDateTime updatedBefore = LocalDateTime.now().minusSeconds(statusCheckMinAgeSeconds);

        List<PaymentTransaction> initiatedPayments = paymentTransactionRepository.findByStatusAndUpdatedAtBefore(
                PaymentStatus.INITIATED,
                updatedBefore
        );

        initiatedPayments.forEach(this::checkInitiatedPayment);
    }

    private void checkInitiatedPayment(PaymentTransaction paymentTransaction) {
        try {
            PaymentPlugin plugin = getSelectedPaymentPlugin(paymentTransaction);

            PaymentPluginStatusCheckResponse response = pluginHttpClient.post(
                    plugin,
                    PAYMENT_STATUS_CHECK_ENDPOINT,
                    new PaymentPluginStatusCheckRequest(paymentTransaction.getId()),
                    PaymentPluginStatusCheckResponse.class
            );

            processStatusCheckResponse(
                    paymentTransaction,
                    response,
                    plugin
            );
        } catch (Exception exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_STATUS_CHECK_FAILED,
                    "reason={} paymentId={} error={}",
                    LogStrings.Reason.PAYMENT_STATUS_CHECK_FAILED,
                    paymentTransaction.getId(),
                    exception.getMessage()
            );
        }
    }

    private void processStatusCheckResponse(
            PaymentTransaction paymentTransaction,
            PaymentPluginStatusCheckResponse response,
            PaymentPlugin plugin
    ) {
        if (response == null || response.status() == null) {
            throw new BadRequestException("Payment plugin status check response is empty.");
        }

        if (PaymentStatus.INITIATED.equals(response.status())) {
            return;
        }

        paymentPluginCallbackService.processCallback(
                paymentTransaction.getId(),
                new PaymentPluginCallbackRequest(
                        response.status(),
                        response.message()
                ),
                plugin.getCode()
        );
    }

    private PaymentPlugin getSelectedPaymentPlugin(PaymentTransaction paymentTransaction) {
        String selectedPaymentMethodCode = paymentTransaction.getSelectedPaymentMethodCode();

        if (selectedPaymentMethodCode == null || selectedPaymentMethodCode.isBlank()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_STATUS_CHECK_FAILED,
                    "reason={} paymentId={}",
                    LogStrings.Reason.PAYMENT_METHOD_NOT_SELECTED,
                    paymentTransaction.getId()
            );

            throw new BadRequestException("Payment method was not selected.");
        }

        return paymentTransaction.getSellerAccount()
                .getPaymentMethods()
                .stream()
                .filter(sellerPaymentMethod -> sellerPaymentMethod.getPaymentMethod()
                        .getCode()
                        .equals(selectedPaymentMethodCode))
                .findFirst()
                .map(MerchantSellerPaymentMethod::getPaymentMethod)
                .map(paymentMethod -> paymentMethod.getPlugin())
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_STATUS_CHECK_FAILED,
                            "reason={} paymentId={} selectedPaymentMethodCode={}",
                            LogStrings.Reason.SELECTED_PAYMENT_METHOD_NOT_AVAILABLE,
                            paymentTransaction.getId(),
                            selectedPaymentMethodCode
                    );

                    return new BadRequestException("Selected payment method is not available.");
                });
    }

}