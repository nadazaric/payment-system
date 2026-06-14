package com.sep.bank.back.feature_qr.service.impl;

import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_qr.dto.QrPaymentPageStatusResponse;
import com.sep.bank.back.feature_qr.service.interf.QrPaymentPageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class QrPaymentPageStatusServiceImpl implements QrPaymentPageStatusService {

    @Autowired
    PaymentRepository paymentRepository;

    @Override
    public Optional<QrPaymentPageStatusResponse> getPaymentPageStatus(UUID paymentId) {
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);

        if (paymentOptional.isEmpty()) {
            return Optional.empty();
        }

        Payment payment = paymentOptional.get();

        if (PaymentStatus.CREATED.equals(payment.getStatus())
                && payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
        }

        return Optional.of(buildStatusResponse(payment));
    }

    private QrPaymentPageStatusResponse buildStatusResponse(Payment payment) {
        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            return new QrPaymentPageStatusResponse(
                    payment.getStatus().name(),
                    "Payment completed successfully.",
                    payment.getSuccessUrl()
            );
        }

        if (PaymentStatus.FAILED.equals(payment.getStatus())) {
            return new QrPaymentPageStatusResponse(
                    payment.getStatus().name(),
                    "Payment failed.",
                    payment.getFailUrl()
            );
        }

        if (PaymentStatus.ERROR.equals(payment.getStatus())) {
            return new QrPaymentPageStatusResponse(
                    payment.getStatus().name(),
                    "Payment processing error.",
                    payment.getErrorUrl()
            );
        }

        if (PaymentStatus.EXPIRED.equals(payment.getStatus())) {
            return new QrPaymentPageStatusResponse(
                    payment.getStatus().name(),
                    "Payment has expired.",
                    payment.getFailUrl()
            );
        }

        return new QrPaymentPageStatusResponse(
                payment.getStatus().name(),
                "Waiting for payment confirmation.",
                null
        );
    }

}