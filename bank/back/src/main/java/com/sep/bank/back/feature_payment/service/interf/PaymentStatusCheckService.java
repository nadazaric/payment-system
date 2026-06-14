package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.dto.PaymentStatusCheckRequest;
import com.sep.bank.back.feature_payment.dto.PaymentStatusCheckResponse;

public interface PaymentStatusCheckService {

    PaymentStatusCheckResponse checkPaymentStatus(PaymentStatusCheckRequest request);

}