package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.model.BankAccount;
import com.sep.bank.back.feature_payment.model.Payment;

public interface PaymentProcessingSupportService {

    void validatePaymentIsAvailableForProcessing(Payment payment, String rejectionAction);

    void validatePaymentNotExpired(Payment payment, String rejectionAction);

    void validatePaymentMethod(
            Payment payment,
            PaymentMethod expectedPaymentMethod,
            String rejectionAction
    );

    void validateCurrency(
            Payment payment,
            BankAccount customerAccount,
            BankAccount merchantAccount,
            String rejectionAction
    );

    void validateSufficientFunds(
            Payment payment,
            BankAccount customerAccount,
            String rejectionAction
    );

    void validatePayerAccountIsDifferentFromMerchantAccount(
            Payment payment,
            BankAccount payerAccount,
            BankAccount merchantAccount,
            String rejectionAction
    );

    void rejectPaymentAsFailed(
            Payment payment,
            String rejectionAction,
            String reason,
            String message
    );

    String rejectPaymentAsError(
            Payment payment,
            Exception exception,
            String rejectionAction,
            String errorReason
    );

}