package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.CardPaymentSubmitRequest;
import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.BankAccount;
import com.sep.bank.back.feature_payment.model.Merchant;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.model.PaymentCard;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentCardRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.CardPaymentProcessingService;
import com.sep.bank.back.feature_payment.service.interf.CardSecurityService;
import com.sep.bank.back.shared.exception.CardPaymentRejectedException;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.text.Normalizer;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardPaymentProcessingServiceImpl implements CardPaymentProcessingService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentCardRepository paymentCardRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    CardSecurityService cardSecurityService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional(noRollbackFor = CardPaymentRejectedException.class)
    public String submitCardPayment(UUID paymentId, CardPaymentSubmitRequest request) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.CARD_PAYMENT_SUBMIT_RECEIVED,
                "bankPaymentId={} pan={} cardHolderName={} expirationDate={}",
                paymentId,
                request.pan(),
                request.cardHolderName(),
                request.expirationDate()
        );

        Payment payment = findPayment(paymentId);

        validatePaymentIsAvailableForProcessing(payment);
        validatePaymentNotExpired(payment);
        validateCardPaymentMethod(payment);

        String normalizedPan = validateAndNormalizePan(payment, request.pan());
        PaymentCard paymentCard = findPaymentCard(payment, normalizedPan);

        validateSecurityCode(payment, paymentCard, request);
        validateCardHolderName(payment, paymentCard, request);
        validateExpirationDate(payment, paymentCard, request);
        validateCardAndAccountAreActive(payment, paymentCard);

        transferFunds(payment, paymentCard);
        completePaymentSuccessfully(payment);

        return payment.getSuccessUrl();
    }

    private Payment findPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.CARD_PAYMENT_REJECTED,
                            "reason={} bankPaymentId={}",
                            LogStrings.Reason.PAYMENT_NOT_FOUND,
                            paymentId
                    );

                    return new IllegalArgumentException("Payment not found.");
                });
    }

    private void validatePaymentIsAvailableForProcessing(Payment payment) {
        if (!PaymentStatus.CREATED.equals(payment.getStatus())
                || Boolean.TRUE.equals(payment.getPaymentAttemptUsed())) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.CARD_PAYMENT_REJECTED,
                    "reason={} bankPaymentId={} status={} paymentAttemptUsed={}",
                    LogStrings.Reason.PAYMENT_NOT_AVAILABLE_FOR_PROCESSING,
                    payment.getId(),
                    payment.getStatus(),
                    payment.getPaymentAttemptUsed()
            );

            throw new IllegalArgumentException("Payment is not available for processing.");
        }
    }

    private void validatePaymentNotExpired(Payment payment) {
        if (!payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            return;
        }

        payment.setStatus(PaymentStatus.EXPIRED);
        paymentRepository.save(payment);

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.CARD_PAYMENT_REJECTED,
                "reason={} bankPaymentId={} expiresAt={}",
                LogStrings.Reason.PAYMENT_EXPIRED,
                payment.getId(),
                payment.getExpiresAt()
        );

        throw new IllegalArgumentException("Payment has expired.");
    }

    private void validateCardPaymentMethod(Payment payment) {
        if (PaymentMethod.CARD.equals(payment.getPaymentMethod())) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.CARD_PAYMENT_REJECTED,
                "reason={} bankPaymentId={} paymentMethod={}",
                LogStrings.Reason.INVALID_PAYMENT_METHOD,
                payment.getId(),
                payment.getPaymentMethod()
        );

        throw new IllegalArgumentException("Payment method is not valid for card processing.");
    }

    private String validateAndNormalizePan(Payment payment, String pan) {
        if (pan == null || pan.isBlank()) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.INVALID_PAN,
                    "Card number is not valid."
            );
        }

        String normalizedPan = pan.replaceAll("\\s+", "");

        if (!normalizedPan.matches("\\d{16}") || !isLuhnValid(normalizedPan)) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.PAN_LUHN_VALIDATION_FAILED,
                    "Card number is not valid."
            );
        }

        return normalizedPan;
    }

    private boolean isLuhnValid(String pan) {
        int sum = 0;
        boolean shouldDouble = false;

        for (int index = pan.length() - 1; index >= 0; index--) {
            int digit = Character.getNumericValue(
                    pan.charAt(index)
            );

            if (shouldDouble) {
                digit *= 2;

                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            shouldDouble = !shouldDouble;
        }

        return sum % 10 == 0;
    }

    private PaymentCard findPaymentCard(Payment payment, String normalizedPan) {
        Optional<PaymentCard> paymentCardOptional = paymentCardRepository.findByPan(normalizedPan);

        if (paymentCardOptional.isPresent()) {
            return paymentCardOptional.get();
        }

        rejectPaymentAsFailed(
                payment,
                LogStrings.Reason.CARD_NOT_FOUND,
                "Card was not found."
        );

        throw new IllegalArgumentException("Card was not found.");
    }

    private void validateSecurityCode(
            Payment payment,
            PaymentCard paymentCard,
            CardPaymentSubmitRequest request
    ) {
        boolean securityCodeValid = cardSecurityService.isSecurityCodeValid(paymentCard, request.securityCode());

        if (securityCodeValid) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                LogStrings.Reason.INVALID_SECURITY_CODE,
                "Security code is not valid."
        );
    }

    private void validateCardHolderName(
            Payment payment,
            PaymentCard paymentCard,
            CardPaymentSubmitRequest request
    ) {
        String requestCardHolderName = normalizeCardHolderName(request.cardHolderName());
        String storedCardHolderName = normalizeCardHolderName(paymentCard.getCardHolderName());

        if (storedCardHolderName.equals(requestCardHolderName)) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                LogStrings.Reason.INVALID_CARD_HOLDER_NAME,
                "Card holder name is not valid."
        );
    }

    private String normalizeCardHolderName(String cardHolderName) {
        if (cardHolderName == null) {
            return "";
        }

        String normalized = Normalizer.normalize(
                cardHolderName,
                Normalizer.Form.NFD
        );

        return normalized
                .replaceAll("\\p{M}", "")
                .replace("đ", "d")
                .replace("Đ", "D")
                .trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private void validateExpirationDate(
            Payment payment,
            PaymentCard paymentCard,
            CardPaymentSubmitRequest request
    ) {
        String expirationDate = request.expirationDate();

        if (expirationDate == null || !expirationDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.INVALID_EXPIRATION_DATE_FORMAT,
                    "Expiration date format is not valid."
            );
        }

        int requestExpirationMonth = Integer.parseInt(expirationDate.substring(0, 2));
        int requestExpirationYear = 2000 + Integer.parseInt(expirationDate.substring(3, 5));

        if (!paymentCard.getExpirationMonth().equals(requestExpirationMonth)
                || !paymentCard.getExpirationYear().equals(requestExpirationYear)) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.CARD_EXPIRATION_DATE_MISMATCH,
                    "Expiration date does not match card data."
            );
        }

        YearMonth cardExpiration = YearMonth.of(paymentCard.getExpirationYear(), paymentCard.getExpirationMonth());

        if (cardExpiration.isBefore(YearMonth.now())) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.CARD_EXPIRED,
                    "Card has expired."
            );
        }
    }

    private void validateCardAndAccountAreActive(Payment payment, PaymentCard paymentCard) {
        if (!Boolean.TRUE.equals(paymentCard.getActive())) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.PAYMENT_CARD_INACTIVE,
                    "Payment card is not active."
            );
        }

        if (!Boolean.TRUE.equals(paymentCard.getBankAccount().getActive())) {
            rejectPaymentAsFailed(
                    payment,
                    LogStrings.Reason.BANK_ACCOUNT_INACTIVE,
                    "Bank account is not active."
            );
        }
    }

    private void transferFunds(Payment payment, PaymentCard paymentCard) {
        BankAccount customerAccount = paymentCard.getBankAccount();

        Merchant merchant = findMerchant(payment);
        BankAccount merchantAccount = merchant.getBankAccount();

        validateCurrency(
                payment,
                customerAccount,
                merchantAccount
        );

        validateSufficientFunds(payment, customerAccount);

        customerAccount.setBalance(customerAccount.getBalance().subtract(payment.getAmount()));
        merchantAccount.setBalance(merchantAccount.getBalance().add(payment.getAmount()));
    }

    private Merchant findMerchant(Payment payment) {
        return merchantRepository.findByBankMerchantId(payment.getBankMerchantId())
                .orElseThrow(() -> {
                    rejectPaymentAsFailed(
                            payment,
                            LogStrings.Reason.MERCHANT_NOT_FOUND,
                            "Merchant was not found."
                    );

                    return new IllegalArgumentException("Merchant was not found.");
                });
    }

    private void validateCurrency(
            Payment payment,
            BankAccount customerAccount,
            BankAccount merchantAccount
    ) {
        boolean currencyValid = payment.getCurrency().equals(customerAccount.getCurrency())
                && payment.getCurrency().equals(merchantAccount.getCurrency());

        if (currencyValid) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                LogStrings.Reason.CURRENCY_MISMATCH,
                "Payment currency does not match account currency."
        );
    }

    private void validateSufficientFunds(Payment payment, BankAccount customerAccount) {
        BigDecimal customerBalance = customerAccount.getBalance();
        BigDecimal paymentAmount = payment.getAmount();

        if (customerBalance.compareTo(paymentAmount) >= 0) {
            return;
        }

        rejectPaymentAsFailed(
                payment,
                LogStrings.Reason.INSUFFICIENT_FUNDS,
                "Insufficient funds."
        );
    }

    private void rejectPaymentAsFailed(Payment payment, String reason, String message) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setPaymentAttemptUsed(true);

        paymentRepository.save(payment);

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.CARD_PAYMENT_REJECTED,
                "reason={} bankPaymentId={}",
                reason,
                payment.getId()
        );

        throw new CardPaymentRejectedException(message, payment.getFailUrl());
    }

    private void completePaymentSuccessfully(Payment payment) {
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentAttemptUsed(true);
        payment.setGlobalTransactionId(UUID.randomUUID().toString());
        payment.setAcquirerTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.CARD_PAYMENT_COMPLETED,
                "bankPaymentId={} globalTransactionId={} acquirerTimestamp={}",
                payment.getId(),
                payment.getGlobalTransactionId(),
                payment.getAcquirerTimestamp()
        );
    }

}