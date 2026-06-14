package com.sep.psp.back.feature_payment.service.impl;

import com.sep.psp.back.feature_merchant.model.MerchantSellerPaymentMethod;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackRequest;
import com.sep.psp.back.feature_payment.dto.plugin.PaymentPluginCallbackResponse;
import com.sep.psp.back.feature_payment.enumeration.PaymentStatus;
import com.sep.psp.back.feature_payment.model.PaymentTransaction;
import com.sep.psp.back.feature_payment.repository.PaymentTransactionRepository;
import com.sep.psp.back.feature_payment.service.interf.PaymentPluginCallbackService;
import com.sep.psp.back.feature_payment.service.interf.PaymentResultNotificationPublisher;
import com.sep.psp.back.shared.error.exception.BadRequestException;
import com.sep.psp.back.shared.logging.LogStrings;
import com.sep.psp.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentPluginCallbackServiceImpl implements PaymentPluginCallbackService {

    @Autowired
    PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Autowired
    PaymentResultNotificationPublisher paymentResultNotificationPublisher;

    @Override
    @Transactional
    public PaymentPluginCallbackResponse processCallback(String paymentId, PaymentPluginCallbackRequest request, String pluginCode) {
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment transaction not found."));

        validateCallback(paymentTransaction, request.status(), pluginCode);

        if (paymentTransaction.getStatus() == request.status()) {
            return new PaymentPluginCallbackResponse(
                    paymentTransaction.getId(),
                    paymentTransaction.getStatus()
            );
        }

        paymentTransaction.setStatus(request.status());

        PaymentTransaction savedPaymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        paymentResultNotificationPublisher.publishPaymentResult(savedPaymentTransaction, request.message());

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_RESULT_PROCESSED,
                "paymentId={} merchantId={} sellerReference={} selectedPaymentMethodCode={} status={} message={}",
                savedPaymentTransaction.getId(),
                savedPaymentTransaction.getMerchant().getMerchantId(),
                savedPaymentTransaction.getSellerAccount().getSellerReference(),
                savedPaymentTransaction.getSelectedPaymentMethodCode(),
                savedPaymentTransaction.getStatus(),
                request.message()
        );

        return new PaymentPluginCallbackResponse(
                savedPaymentTransaction.getId(),
                savedPaymentTransaction.getStatus()
        );
    }

    private void validateCallback(PaymentTransaction paymentTransaction, PaymentStatus receivedStatus, String pluginCode) {
        validateFinalPaymentStatus(paymentTransaction, receivedStatus);

        validatePaymentStatusTransition(paymentTransaction, receivedStatus);

        validateCallbackPlugin(paymentTransaction, pluginCode);
    }

    private void validateFinalPaymentStatus(PaymentTransaction paymentTransaction, PaymentStatus receivedStatus) {
        if (receivedStatus == PaymentStatus.SUCCESS || receivedStatus == PaymentStatus.FAILED || receivedStatus == PaymentStatus.ERROR) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_RESULT_REJECTED,
                "reason={} paymentId={} receivedStatus={}",
                LogStrings.Reason.INVALID_PAYMENT_RESULT_STATUS,
                paymentTransaction.getId(),
                receivedStatus
        );

        throw new BadRequestException("Payment result status is not valid.");
    }

    private void validatePaymentStatusTransition(PaymentTransaction paymentTransaction, PaymentStatus receivedStatus) {
        PaymentStatus currentStatus = paymentTransaction.getStatus();

        if (currentStatus == PaymentStatus.INITIATED) {
            return;
        }

        if (currentStatus == receivedStatus) {
            return;
        }

        if (currentStatus == PaymentStatus.CREATED) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_RESULT_REJECTED,
                    "reason={} paymentId={} currentStatus={} receivedStatus={}",
                    LogStrings.Reason.PAYMENT_NOT_INITIATED,
                    paymentTransaction.getId(),
                    currentStatus,
                    receivedStatus
            );

            throw new BadRequestException("Payment is not initiated.");
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_RESULT_REJECTED,
                "reason={} paymentId={} currentStatus={} receivedStatus={}",
                LogStrings.Reason.PAYMENT_ALREADY_COMPLETED,
                paymentTransaction.getId(),
                currentStatus,
                receivedStatus
        );

        throw new BadRequestException("Payment result cannot be changed.");
    }

    private void validateCallbackPlugin(PaymentTransaction paymentTransaction, String pluginCode) {
        String expectedPluginCode = getExpectedPluginCode(paymentTransaction);

        if (expectedPluginCode.equals(pluginCode)) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_RESULT_REJECTED,
                "reason={} paymentId={} expectedPluginCode={} receivedPluginCode={}",
                LogStrings.Reason.PAYMENT_PLUGIN_MISMATCH,
                paymentTransaction.getId(),
                expectedPluginCode,
                pluginCode
        );

        throw new BadRequestException("Payment plugin does not match initiated payment.");
    }

    private String getExpectedPluginCode(PaymentTransaction paymentTransaction) {
        String selectedPaymentMethodCode = paymentTransaction.getSelectedPaymentMethodCode();

        if (selectedPaymentMethodCode == null || selectedPaymentMethodCode.isBlank()) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_RESULT_REJECTED,
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
                .map(sellerPaymentMethod -> sellerPaymentMethod.getPaymentMethod()
                        .getPlugin()
                        .getCode())
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_RESULT_REJECTED,
                            "reason={} paymentId={} selectedPaymentMethodCode={}",
                            LogStrings.Reason.SELECTED_PAYMENT_METHOD_NOT_AVAILABLE,
                            paymentTransaction.getId(),
                            selectedPaymentMethodCode
                    );

                    return new BadRequestException("Selected payment method is not available.");
                });
    }

}