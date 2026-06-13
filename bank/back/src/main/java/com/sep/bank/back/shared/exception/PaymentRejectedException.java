package com.sep.bank.back.shared.exception;

import lombok.Getter;

@Getter
public class PaymentRejectedException extends RuntimeException {

    private final String redirectUrl;

    public PaymentRejectedException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

}