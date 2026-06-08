package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.psp.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.psp.back.feature_payment.dto.PaymentTransactionResponse;

public interface PaymentTransactionService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    PaymentTransactionResponse getPayment(String paymentId);

}