package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.ExpiredPaymentService;
import com.sep.bank.back.feature_payment.service.interf.PaymentCallbackService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpiredPaymentServiceImpl implements ExpiredPaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentCallbackService paymentCallbackService;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public void processExpiredPayments() {
        List<Payment> expiredPayments = paymentRepository.findByStatusAndPaymentAttemptUsedFalseAndExpiresAtBefore(
                PaymentStatus.CREATED,
                LocalDateTime.now()
        );

        expiredPayments.forEach(this::expirePayment);
    }

    private void expirePayment(Payment payment) {
        payment.setStatus(PaymentStatus.EXPIRED);

        paymentRepository.save(payment);

        paymentCallbackService.sendPaymentResultCallback(payment, "Payment link has expired.");

        appLoggerService.info(
                LogStrings.Feature.SCHEDULING,
                LogStrings.Action.PAYMENT_EXPIRED,
                "bankPaymentId={} expiresAt={}",
                payment.getId(),
                payment.getExpiresAt()
        );
    }

}