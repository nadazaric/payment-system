package com.sep.bank.back.feature_payment.service.interf;

import com.sep.bank.back.feature_payment.model.PaymentCard;

public interface CardSecurityService {

    boolean isSecurityCodeValid(PaymentCard paymentCard, String securityCode);

}