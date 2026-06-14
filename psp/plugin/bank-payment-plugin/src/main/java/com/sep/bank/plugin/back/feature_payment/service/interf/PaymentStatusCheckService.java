package com.sep.bank.plugin.back.feature_payment.service.interf;

import com.sep.bank.plugin.back.feature_payment.dto.psp.PspPaymentStatusCheckRequest;
import com.sep.bank.plugin.back.feature_payment.dto.psp.PspPaymentStatusCheckResponse;

public interface PaymentStatusCheckService {

    PspPaymentStatusCheckResponse checkPaymentStatus(PspPaymentStatusCheckRequest request);

}