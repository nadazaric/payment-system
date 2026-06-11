package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;

public interface PaymentService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request);

}