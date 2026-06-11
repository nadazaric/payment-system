package com.sep.web_shop.back.feature_reservation.service.impl;

import com.sep.web_shop.back.feature_reservation.enumeration.PaymentStatus;
import com.sep.web_shop.back.feature_reservation.event.PaymentResultNotificationEvent;
import com.sep.web_shop.back.feature_reservation.model.Reservation;
import com.sep.web_shop.back.feature_reservation.repository.ReservationRepository;
import com.sep.web_shop.back.feature_reservation.service.interf.ReservationPaymentNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationPaymentNotificationServiceImpl implements ReservationPaymentNotificationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Value("${app.psp.merchant-id}")
    String expectedMerchantId;

    @Value("${app.psp.seller-reference}")
    String expectedSellerReference;

    @Override
    @Transactional
    public void processPaymentResult(PaymentResultNotificationEvent event) {
        validateMerchant(event);
        validateSeller(event);
        validateFinalPaymentStatus(event.status());

        Reservation reservation = reservationRepository
                .findByPspPaymentIdAndMerchantOrderId(
                        event.paymentId(),
                        event.merchantOrderId()
                )
                .orElseThrow(() -> new IllegalArgumentException("Reservation payment not found."));

        validatePaymentStatusTransition(
                reservation,
                event.status()
        );

        if (reservation.getPaymentStatus() == event.status()) {
            return;
        }

        reservation.setPaymentStatus(event.status());
        reservation.setPaymentMethodCode(event.paymentMethodCode());

        reservationRepository.save(reservation);
    }

    private void validateMerchant(PaymentResultNotificationEvent event) {
        if (expectedMerchantId.equals(event.merchantId())) {
            return;
        }

        throw new IllegalArgumentException("Payment notification merchant does not match web-shop merchant.");
    }

    private void validateSeller(PaymentResultNotificationEvent event) {
        if (expectedSellerReference.equals(event.sellerReference())) {
            return;
        }

        throw new IllegalArgumentException("Payment notification seller does not match web-shop seller.");
    }

    private void validateFinalPaymentStatus(PaymentStatus status) {
        if (status == PaymentStatus.SUCCESS
                || status == PaymentStatus.FAILED
                || status == PaymentStatus.ERROR) {
            return;
        }

        throw new IllegalArgumentException("Invalid payment notification status.");
    }

    private void validatePaymentStatusTransition(
            Reservation reservation,
            PaymentStatus receivedStatus
    ) {
        PaymentStatus currentStatus = reservation.getPaymentStatus();

        if (currentStatus == PaymentStatus.INITIATED) {
            return;
        }

        if (currentStatus == receivedStatus) {
            return;
        }

        throw new IllegalArgumentException("Reservation payment status cannot be changed.");
    }

}