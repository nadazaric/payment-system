package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.dto.CardPaymentSubmitRequest;

import java.util.UUID;

public interface CardPaymentProcessingService {

    String submitCardPayment(UUID paymentId, CardPaymentSubmitRequest request);

}