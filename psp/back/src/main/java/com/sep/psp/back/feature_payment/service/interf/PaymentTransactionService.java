package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_payment.dto.*;

public interface PaymentTransactionService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    PaymentDetailsResponse getPayment(String paymentId);

    InitiatePaymentResponse initiatePayment(String paymentId, InitiatePaymentRequest request);

}