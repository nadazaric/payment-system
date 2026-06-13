package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.PaymentStatusCheckRequest;
import com.sep.bank.back.feature_payment.dto.PaymentStatusCheckResponse;
import com.sep.bank.back.feature_payment.enumeration.PaymentStatus;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentStatusCheckService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentStatusCheckServiceImpl implements PaymentStatusCheckService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Override
    @Transactional
    public PaymentStatusCheckResponse checkPaymentStatus(PaymentStatusCheckRequest request) {
        Payment payment = findPayment(request);

        expirePaymentIfNeeded(payment);

        return new PaymentStatusCheckResponse(
                mapStatusForPlugin(payment.getStatus()),
                buildStatusMessage(payment),
                payment.getGlobalTransactionId(),
                payment.getAcquirerTimestamp()
        );
    }

    private Payment findPayment(PaymentStatusCheckRequest request) {
        return paymentRepository.findByBankMerchantIdAndStanAndPspTimestamp(
                        request.bankMerchantId(),
                        request.stan(),
                        request.pspTimestamp()
                )
                .orElseThrow(() -> {
                    appLoggerService.warn(
                            LogStrings.Feature.PAYMENT,
                            LogStrings.Action.PAYMENT_STATUS_CHECK_REJECTED,
                            "reason={} bankMerchantId={} stan={} pspTimestamp={}",
                            LogStrings.Reason.PAYMENT_NOT_FOUND,
                            request.bankMerchantId(),
                            request.stan(),
                            request.pspTimestamp()
                    );

                    return new IllegalArgumentException("Payment was not found.");
                });
    }

    private void expirePaymentIfNeeded(Payment payment) {
        if (!PaymentStatus.CREATED.equals(payment.getStatus())) {
            return;
        }

        if (!payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            return;
        }

        payment.setStatus(PaymentStatus.EXPIRED);

        paymentRepository.save(payment);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_EXPIRED,
                "bankPaymentId={} expiresAt={}",
                payment.getId(),
                payment.getExpiresAt()
        );
    }

    private String mapStatusForPlugin(PaymentStatus status) {
        if (PaymentStatus.CREATED.equals(status)) {
            return "INITIATED";
        }

        if (PaymentStatus.EXPIRED.equals(status)) {
            return "FAILED";
        }

        return status.name();
    }

    private String buildStatusMessage(Payment payment) {
        if (PaymentStatus.CREATED.equals(payment.getStatus())) {
            return "Payment is still waiting for bank result.";
        }

        if (PaymentStatus.EXPIRED.equals(payment.getStatus())) {
            return "Payment failed.";
        }

        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            return "Payment completed successfully.";
        }

        if (PaymentStatus.FAILED.equals(payment.getStatus())) {
            return "Payment failed.";
        }

        return "Payment processing error.";
    }

}