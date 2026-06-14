package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.BankAccount;
import com.sep.bank.back.feature_payment.model.Merchant;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.BankAccountRepository;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentCallbackService;
import com.sep.bank.back.feature_payment.service.interf.PaymentProcessingSupportService;
import com.sep.bank.back.feature_qr.dto.IpsQrPayloadData;
import com.sep.bank.back.feature_qr.dto.QrPaymentRequest;
import com.sep.bank.back.feature_qr.dto.QrPaymentResponse;
import com.sep.bank.back.feature_qr.service.interf.IpsQrValidatorService;
import com.sep.bank.back.feature_qr.service.interf.QrPaymentProcessingService;
import com.sep.bank.back.shared.exception.PaymentRejectedException;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class QrPaymentProcessingServiceImpl implements QrPaymentProcessingService {

    private static final String PAYMENT_COMPLETED_MESSAGE = "QR payment completed successfully.";
    private static final String PAYMENT_ERROR_MESSAGE = "QR payment processing error.";

    @Autowired
    IpsQrValidatorService ipsQrValidatorService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    PaymentProcessingSupportService paymentProcessingSupportService;

    @Autowired
    PaymentCallbackService paymentCallbackService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional(noRollbackFor = PaymentRejectedException.class)
    public QrPaymentResponse submitQrPayment(QrPaymentRequest request) {
        Payment payment = null;

        try {
            IpsQrPayloadData payloadData = ipsQrValidatorService.validateAndParse(request.payload());

            appLoggerService.info(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.QR_PAYMENT_SUBMIT_RECEIVED,
                    "paymentReference={} payerAccountId={} amount={} currency={}",
                    payloadData.paymentReference(),
                    request.payerAccountId(),
                    payloadData.amount(),
                    payloadData.currency()
            );

            payment = findPayment(payloadData.paymentReference());

            paymentProcessingSupportService.validatePaymentIsAvailableForProcessing(payment, LogStrings.Action.QR_PAYMENT_REJECTED);
            paymentProcessingSupportService.validatePaymentNotExpired(payment, LogStrings.Action.QR_PAYMENT_REJECTED);
            paymentProcessingSupportService.validatePaymentMethod(payment, PaymentMethod.QR, LogStrings.Action.QR_PAYMENT_REJECTED);

            validateQrPayloadMatchesPayment(payment, payloadData);

            BankAccount payerAccount = findPayerAccount(payment, request.payerAccountId());
            Merchant merchant = findMerchant(payment);
            BankAccount merchantAccount = merchant.getBankAccount();

            validateRecipientAccount(payment, payloadData, merchantAccount);
            validatePayerAccountActive(payment, payerAccount);

            paymentProcessingSupportService.validatePayerAccountIsDifferentFromMerchantAccount(
                    payment,
                    payerAccount,
                    merchantAccount,
                    LogStrings.Action.QR_PAYMENT_REJECTED
            );

            paymentProcessingSupportService.validateCurrency(
                    payment,
                    payerAccount,
                    merchantAccount,
                    LogStrings.Action.QR_PAYMENT_REJECTED
            );

            paymentProcessingSupportService.validateSufficientFunds(
                    payment,
                    payerAccount,
                    LogStrings.Action.QR_PAYMENT_REJECTED
            );

            transferFunds(payment, payerAccount, merchantAccount);
            completePaymentSuccessfully(payment);

            paymentCallbackService.sendPaymentResultCallback(payment, PAYMENT_COMPLETED_MESSAGE);
            return new QrPaymentResponse(
                    PaymentStatus.SUCCESS.name(),
                    PAYMENT_COMPLETED_MESSAGE
            );
        } catch (PaymentRejectedException exception) {
            return new QrPaymentResponse(
                    PaymentStatus.FAILED.name(),
                    exception.getMessage()
            );
        } catch (IllegalArgumentException exception) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.QR_PAYMENT_REJECTED,
                    "reason={} bankPaymentId={} error={}",
                    LogStrings.Reason.QR_PAYMENT_PROCESSING_ERROR,
                    payment == null ? null : payment.getId(),
                    exception.getMessage()
            );

            return new QrPaymentResponse(
                    PaymentStatus.FAILED.name(),
                    exception.getMessage()
            );
        } catch (Exception exception) {
            paymentProcessingSupportService.rejectPaymentAsError(
                    payment,
                    exception,
                    LogStrings.Action.QR_PAYMENT_REJECTED,
                    LogStrings.Reason.QR_PAYMENT_PROCESSING_ERROR
            );

            return new QrPaymentResponse(
                    PaymentStatus.ERROR.name(),
                    PAYMENT_ERROR_MESSAGE
            );
        }
    }

    private Payment findPayment(String qrPaymentReference) {
        return paymentRepository.findByQrPaymentReference(qrPaymentReference)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.QR_PAYMENT_REJECTED,
                            "reason={} paymentReference={}",
                            LogStrings.Reason.QR_PAYMENT_NOT_FOUND,
                            qrPaymentReference
                    );

                    return new IllegalArgumentException("QR payment was not found.");
                });
    }

    private BankAccount findPayerAccount(
            Payment payment,
            UUID payerAccountId
    ) {
        return bankAccountRepository.findById(payerAccountId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.QR_PAYMENT_REJECTED,
                            "reason={} bankPaymentId={} payerAccountId={}",
                            LogStrings.Reason.PAYER_ACCOUNT_NOT_FOUND,
                            payment.getId(),
                            payerAccountId
                    );

                    return new IllegalArgumentException("Payer account was not found.");
                });
    }

    private Merchant findMerchant(Payment payment) {
        return merchantRepository.findByBankMerchantId(payment.getBankMerchantId())
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.QR_PAYMENT_REJECTED,
                            "reason={} bankPaymentId={} bankMerchantId={}",
                            LogStrings.Reason.MERCHANT_NOT_FOUND,
                            payment.getId(),
                            payment.getBankMerchantId()
                    );

                    return new IllegalArgumentException("Merchant was not found.");
                });
    }

    private void validateQrPayloadMatchesPayment(
            Payment payment,
            IpsQrPayloadData payloadData
    ) {
        boolean amountMatches = payment.getAmount().compareTo(payloadData.amount()) == 0;
        boolean currencyMatches = payment.getCurrency().equals(payloadData.currency());

        if (amountMatches && currencyMatches) {
            return;
        }

        paymentProcessingSupportService.rejectPaymentAsFailed(
                payment,
                LogStrings.Action.QR_PAYMENT_REJECTED,
                LogStrings.Reason.QR_PAYMENT_AMOUNT_MISMATCH,
                "QR payment amount or currency does not match payment."
        );
    }

    private void validateRecipientAccount(
            Payment payment,
            IpsQrPayloadData payloadData,
            BankAccount merchantAccount
    ) {
        String qrRecipientAccount = payloadData.recipientAccount();
        String merchantAccountNumber = merchantAccount.getAccountNumber();

        if (qrRecipientAccount.equals(merchantAccountNumber)) {
            return;
        }

        paymentProcessingSupportService.rejectPaymentAsFailed(
                payment,
                LogStrings.Action.QR_PAYMENT_REJECTED,
                LogStrings.Reason.QR_PAYMENT_RECIPIENT_ACCOUNT_MISMATCH,
                "QR payment recipient account does not match merchant account."
        );
    }

    private void validatePayerAccountActive(Payment payment, BankAccount payerAccount) {
        if (Boolean.TRUE.equals(payerAccount.getActive())) {
            return;
        }

        paymentProcessingSupportService.rejectPaymentAsFailed(
                payment,
                LogStrings.Action.QR_PAYMENT_REJECTED,
                LogStrings.Reason.BANK_ACCOUNT_INACTIVE,
                "Payer account is not active."
        );
    }

    private void transferFunds(
            Payment payment,
            BankAccount payerAccount,
            BankAccount merchantAccount
    ) {
        payerAccount.setBalance(payerAccount.getBalance().subtract(payment.getAmount()));
        merchantAccount.setBalance(merchantAccount.getBalance().add(payment.getAmount()));
    }

    private void completePaymentSuccessfully(Payment payment) {
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentAttemptUsed(true);
        payment.setGlobalTransactionId(UUID.randomUUID().toString());
        payment.setAcquirerTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.QR_PAYMENT_COMPLETED,
                "bankPaymentId={} globalTransactionId={} acquirerTimestamp={}",
                payment.getId(),
                payment.getGlobalTransactionId(),
                payment.getAcquirerTimestamp()
        );
    }

}