package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.dto.CreatePaymentRequest;
import com.sep.bank.back.feature_payment.dto.CreatePaymentResponse;
import com.sep.bank.back.feature_payment.dto.PaymentPageDTO;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    CreatePaymentResponse createPayment(CreatePaymentRequest request);

    Optional<PaymentPageDTO> getPaymentPageData(UUID paymentId);

}