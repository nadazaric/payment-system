package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.model.Payment;

public interface PaymentCallbackService {

    void sendPaymentResultCallback(Payment payment, String message);

}