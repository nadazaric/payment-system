package com.sep.bank.back.feature_payment.service.impl;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.bank.back.feature_payment.model.Payment;
import com.sep.bank.back.feature_payment.repository.MerchantRepository;
import com.sep.bank.back.feature_payment.repository.PaymentRepository;
import com.sep.bank.back.feature_payment.service.interf.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Value("${app.bank.payment-page-base-url}")
    String paymentPageBaseUrl;

    @Override
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
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

        return new CreatePaymentResponse(
                savedPayment.getId().toString(),
                paymentPageBaseUrl + "/" + savedPayment.getId()
        );
    }

    private void validateMerchantExists(String bankMerchantId) {
        boolean merchantExists = merchantRepository.existsByBankMerchantId(bankMerchantId);

        if (!merchantExists) {
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
            throw new IllegalArgumentException("Payment request already exists.");
        }
    }

}