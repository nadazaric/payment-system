package com.sep.bank.plugin.back.feature_payment.service.interf;

import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackRequest;
import com.sep.bank.plugin.back.feature_payment.dto.bank.BankPaymentCallbackResponse;

public interface BankPaymentCallbackService {

    BankPaymentCallbackResponse processBankCallback(BankPaymentCallbackRequest request);

}