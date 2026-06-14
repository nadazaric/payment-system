package com.sep.web_shop.back.feature_reservation.service.impl;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;
import com.sep.web_shop.back.feature_reservation.event.PaymentResultNotificationEvent;
import com.sep.web_shop.back.feature_reservation.model.Reservation;
import com.sep.web_shop.back.feature_reservation.repository.ReservationRepository;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationPaymentNotificationService;
import com.sep.web_shop.back.shared.logging.LogStrings;
import com.sep.web_shop.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationPaymentNotificationServiceImpl implements ReservationPaymentNotificationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.psp.merchant-id}")
    String expectedMerchantId;

    @Value("${app.psp.seller-reference}")
    String expectedSellerReference;

    @Value("${app.psp.currency}")
    String expectedCurrency;

    @Override
    @Transactional
    public void processPaymentResult(PaymentResultNotificationEvent event) {
        validateMerchant(event);
        validateSeller(event);
        validateFinalPaymentStatus(event);

        Reservation reservation = reservationRepository
                .findByPspPaymentIdAndMerchantOrderId(
                        event.paymentId(),
                        event.merchantOrderId()
                )
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                            "reason={} paymentId={} merchantOrderId={}",
                            LogStrings.Reason.RESERVATION_PAYMENT_NOT_FOUND,
                            event.paymentId(),
                            event.merchantOrderId()
                    );

                    return new IllegalArgumentException("Reservation payment not found.");
                });

        validatePaymentAmountAndCurrency(reservation, event);
        validatePaymentStatusTransition(reservation, event.status());

        if (reservation.getPaymentStatus() == event.status()) {
            return;
        }

        reservation.setPaymentStatus(event.status());
        reservation.setPaymentMethodCode(event.paymentMethodCode());

        Reservation savedReservation = reservationRepository.save(reservation);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_PROCESSED,
                "reservationId={} paymentId={} merchantOrderId={} paymentStatus={} paymentMethodCode={}",
                savedReservation.getId(),
                savedReservation.getPspPaymentId(),
                savedReservation.getMerchantOrderId(),
                savedReservation.getPaymentStatus(),
                savedReservation.getPaymentMethodCode()
        );
    }

    private void validateMerchant(PaymentResultNotificationEvent event) {
        if (expectedMerchantId.equals(event.merchantId())) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                "reason={} expectedMerchantId={} receivedMerchantId={} paymentId={} merchantOrderId={}",
                LogStrings.Reason.PAYMENT_NOTIFICATION_MERCHANT_MISMATCH,
                expectedMerchantId,
                event.merchantId(),
                event.paymentId(),
                event.merchantOrderId()
        );

        throw new IllegalArgumentException("Payment notification merchant does not match web-shop merchant.");
    }

    private void validateSeller(PaymentResultNotificationEvent event) {
        if (expectedSellerReference.equals(event.sellerReference())) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                "reason={} expectedSellerReference={} receivedSellerReference={} paymentId={} merchantOrderId={}",
                LogStrings.Reason.PAYMENT_NOTIFICATION_SELLER_MISMATCH,
                expectedSellerReference,
                event.sellerReference(),
                event.paymentId(),
                event.merchantOrderId()
        );

        throw new IllegalArgumentException("Payment notification seller does not match web-shop seller.");
    }

    private void validateFinalPaymentStatus(PaymentResultNotificationEvent event) {
        if (event.status() == PaymentStatus.SUCCESS
                || event.status() == PaymentStatus.FAILED
                || event.status() == PaymentStatus.ERROR) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                "reason={} paymentId={} merchantOrderId={} receivedStatus={}",
                LogStrings.Reason.PAYMENT_NOTIFICATION_INVALID_STATUS,
                event.paymentId(),
                event.merchantOrderId(),
                event.status()
        );

        throw new IllegalArgumentException("Invalid payment notification status.");
    }

    private void validatePaymentStatusTransition(Reservation reservation, PaymentStatus receivedStatus) {
        PaymentStatus currentStatus = reservation.getPaymentStatus();

        if (currentStatus == PaymentStatus.INITIATED) {
            return;
        }

        if (currentStatus == receivedStatus) {
            return;
        }

        appLoggerService.warn(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                "reason={} reservationId={} paymentId={} merchantOrderId={} currentStatus={} receivedStatus={}",
                LogStrings.Reason.RESERVATION_PAYMENT_ALREADY_COMPLETED,
                reservation.getId(),
                reservation.getPspPaymentId(),
                reservation.getMerchantOrderId(),
                currentStatus,
                receivedStatus
        );

        throw new IllegalArgumentException("Reservation payment status cannot be changed.");
    }

    private void validatePaymentAmountAndCurrency(
            Reservation reservation,
            PaymentResultNotificationEvent event
    ) {
        if (event.amount() == null || event.amount().compareTo(reservation.getTotalPrice()) != 0) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                    "reason={} reservationId={} paymentId={} merchantOrderId={} expectedAmount={} receivedAmount={}",
                    LogStrings.Reason.PAYMENT_NOTIFICATION_AMOUNT_MISMATCH,
                    reservation.getId(),
                    event.paymentId(),
                    event.merchantOrderId(),
                    reservation.getTotalPrice(),
                    event.amount()
            );

            throw new IllegalArgumentException("Payment notification amount does not match reservation amount.");
        }

        if (!expectedCurrency.equals(event.currency())) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_NOTIFICATION_REJECTED,
                    "reason={} reservationId={} paymentId={} merchantOrderId={} expectedCurrency={} receivedCurrency={}",
                    LogStrings.Reason.PAYMENT_NOTIFICATION_CURRENCY_MISMATCH,
                    reservation.getId(),
                    event.paymentId(),
                    event.merchantOrderId(),
                    expectedCurrency,
                    event.currency()
            );

            throw new IllegalArgumentException("Payment notification currency does not match expected currency.");
        }
    }

}