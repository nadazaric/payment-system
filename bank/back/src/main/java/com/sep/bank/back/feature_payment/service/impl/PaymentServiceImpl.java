package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentService;
import com.sep.bank.back.shared.logging.LogStrings;
import com.sep.bank.back.shared.logging.service.interf.AppLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    AppLoggerService appLoggerService;

    @Value("${app.bank.payment-page-base-url}")
    String paymentPageBaseUrl;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CREATE_REQUEST_RECEIVED,
                "bankMerchantId={} stan={} pspTimestamp={} paymentMethod={} amount={} currency={}",
                request.bankMerchantId(),
                request.stan(),
                request.pspTimestamp(),
                request.paymentMethod(),
                request.amount(),
                request.currency()
        );

        validateMerchantExists(request.bankMerchantId());
        validateDuplicatePaymentRequest(request);

        Payment payment = new Payment();

        payment.setBankMerchantId(request.bankMerchantId());
        payment.setStan(request.stan());
        payment.setPspTimestamp(request.pspTimestamp());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());

        Payment savedPayment = paymentRepository.save(payment);

        appLoggerService.info(
                LogStrings.Feature.PAYMENT,
                LogStrings.Action.PAYMENT_CREATED,
                "bankPaymentId={} bankMerchantId={} stan={}",
                savedPayment.getId(),
                savedPayment.getBankMerchantId(),
                savedPayment.getStan()
        );

        return new CreatePaymentResponse(
                savedPayment.getId().toString(),
                paymentPageBaseUrl + "/" + savedPayment.getId()
        );
    }

    private void validateMerchantExists(String bankMerchantId) {
        boolean merchantExists = merchantRepository.existsByBankMerchantId(bankMerchantId);

        if (!merchantExists) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} bankMerchantId={}",
                    LogStrings.Reason.BANK_MERCHANT_NOT_FOUND,
                    bankMerchantId
            );

            throw new IllegalArgumentException("Bank merchant does not exist.");
        }
    }

    private void validateDuplicatePaymentRequest(CreatePaymentRequest request) {
        boolean paymentExists = paymentRepository.existsByBankMerchantIdAndStanAndPspTimestamp(
                request.bankMerchantId(),
                request.stan(),
                request.pspTimestamp()
        );

        if (paymentExists) {
            appLoggerService.warn(
                    LogStrings.Feature.PAYMENT,
                    LogStrings.Action.PAYMENT_CREATE_REJECTED,
                    "reason={} bankMerchantId={} stan={} pspTimestamp={}",
                    LogStrings.Reason.PAYMENT_REQUEST_ALREADY_EXISTS,
                    request.bankMerchantId(),
                    request.stan(),
                    request.pspTimestamp()
            );

            throw new IllegalArgumentException("Payment request already exists.");
        }
    }

}