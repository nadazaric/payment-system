package com.sep.psp.back.feature_payment.service.interf;

public interface PaymentStatusCheckService {

    void checkInitiatedPayments();

    void expireCreatedPayments();

}