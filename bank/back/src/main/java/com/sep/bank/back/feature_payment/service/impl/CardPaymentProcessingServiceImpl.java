package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.CardPaymentSubmitRequest;
import com.sep.bank.back.feature_payment.enumeration.PaymentMethod;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.model.PaymentCard;
import com.sep.bank.back.feature_payment.repository.PaymentCardRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.CardPaymentProcessingService;
import com.sep.bank.back.feature_payment.service.interf.CardSecurityService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardPaymentProcessingServiceImpl implements CardPaymentProcessingService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentCardRepository paymentCardRepository;

    @Autowired
    CardSecurityService cardSecurityService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
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

        return "/payments/" + payment.getId();
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

        throw new IllegalArgumentException(message);
    }

}