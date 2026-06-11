package com.sep.psp.back.feature_payment.service.interf;

import com.sep.psp.back.feature_payment.model.PaymentTransaction;

public interface PaymentResultNotificationPublisher {

    void publishPaymentResult(PaymentTransaction paymentTransaction, String message);

}