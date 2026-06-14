package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.BankAccount;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentCallbackService;
import com.sep.bank.back.feature_payment.service.interf.PaymentProcessingSupportService;
import com.sep.bank.back.shared.exception.PaymentRejectedException;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentProcessingSupportServiceImpl implements PaymentProcessingSupportService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentCallbackService paymentCallbackService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    public void validatePaymentIsAvailableForProcessing(Payment payment, String rejectionAction) {
        if (!PaymentStatus.CREATED.equals(payment.getStatus())
                || Boolean.TRUE.equals(payment.getPaymentAttemptUsed())) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    rejectionAction,
                    "reason={} bankPaymentId={} status={} paymentAttemptUsed={}",
                    LogStrings.Reason.PAYMENT_NOT_AVAILABLE_FOR_PROCESSING,
                    payment.getId(),
                    payment.getStatus(),
                    payment.getPaymentAttemptUsed()
            );

            throw new IllegalArgumentException("Payment is not available for processing.");
        }
    }

    @Override
    public void validatePaymentNotExpired(Payment payment, String rejectionAction) {
        if (!payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            return;
        }

        payment.setStatus(PaymentStatus.EXPIRED);
        paymentRepository.save(payment);

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                rejectionAction,
                "reason={} bankPaymentId={} expiresAt={}",
                LogStrings.Reason.PAYMENT_EXPIRED,
                payment.getId(),
                payment.getExpiresAt()
        );

        throw new IllegalArgumentException("Payment has expired.");
    }

    @Override
    public void validatePaymentMethod(
            Payment payment,
            PaymentMethod expectedPaymentMethod,
            String rejectionAction
    ) {
        if (expectedPaymentMethod.equals(payment.getPaymentMethod())) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                rejectionAction,
                "reason={} bankPaymentId={} paymentMethod={}",
                LogStrings.Reason.INVALID_PAYMENT_METHOD,
                payment.getId(),
                payment.getPaymentMethod()
        );

        throw new IllegalArgumentException("Payment method is not valid.");
    }

    @Override
    public void validateCurrency(
            Payment payment,
            BankAccount customerAccount,
            BankAccount merchantAccount,
            String rejectionAction
    ) {
        boolean currencyValid = payment.getCurrency().equals(customerAccount.getCurrency())
                && payment.getCurrency().equals(merchantAccount.getCurrency());

        if (currencyValid) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                rejectionAction,
                LogStrings.Reason.CURRENCY_MISMATCH,
                "Payment currency does not match account currency."
        );
    }

    @Override
    public void validateSufficientFunds(
            Payment payment,
            BankAccount customerAccount,
            String rejectionAction
    ) {
        BigDecimal customerBalance = customerAccount.getBalance();
        BigDecimal paymentAmount = payment.getAmount();

        if (customerBalance.compareTo(paymentAmount) >= 0) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                rejectionAction,
                LogStrings.Reason.INSUFFICIENT_FUNDS,
                "Insufficient funds."
        );
    }

    @Override
    public void validatePayerAccountIsDifferentFromMerchantAccount(
            Payment payment,
            BankAccount payerAccount,
            BankAccount merchantAccount,
            String rejectionAction
    ) {
        if (!payerAccount.getId().equals(merchantAccount.getId())) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                rejectionAction,
                LogStrings.Reason.PAYER_ACCOUNT_NOT_ALLOWED,
                "Payer account cannot be the same as merchant account."
        );
    }

    @Override
    public void rejectPaymentAsFailed(
            Payment payment,
            String rejectionAction,
            String reason,
            String message
    ) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentAttemptUsed(true);
        ensureAcquirerData(payment);

        paymentRepository.save(payment);

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                rejectionAction,
                "reason={} bankPaymentId={}",
                reason,
                payment.getId()
        );

        paymentCallbackService.sendPaymentResultCallback(payment, message);
        throw new PaymentRejectedException(message, payment.getFailUrl());
    }

    @Override
    public String rejectPaymentAsError(
            Payment payment,
            Exception exception,
            String rejectionAction,
            String errorReason
    ) {
        if (payment == null) {
            appLoggerService.error(
                    LogStrings.Feature.PAYMENT,
                    rejectionAction,
                    "reason={} bankPaymentId={} error={}",
                    errorReason,
                    null,
                    exception.getMessage()
            );

            return null;
        }

        payment.setStatus(PaymentStatus.ERROR);
        payment.setPaymentAttemptUsed(true);
        ensureAcquirerData(payment);

        paymentRepository.save(payment);

        appLoggerService.error(
                LogStrings.Feature.PAYMENT,
                rejectionAction,
                "reason={} bankPaymentId={} error={}",
                errorReason,
                payment.getId(),
                exception.getMessage()
        );

        paymentCallbackService.sendPaymentResultCallback(payment, "Payment processing error.");
        return payment.getErrorUrl();
    }

    private void ensureAcquirerData(Payment payment) {
        if (payment.getGlobalTransactionId() == null || payment.getGlobalTransactionId().isBlank()) {
            payment.setGlobalTransactionId(UUID.randomUUID().toString());
        }

        if (payment.getAcquirerTimestamp() == null) {
            payment.setAcquirerTimestamp(LocalDateTime.now());
        }
    }

}