package com.sep.bank.back.shared.exception;

import lombok.Getter;

@Getter
public class CardPaymentRejectedException extends RuntimeException {

    private final String redirectUrl;

    public CardPaymentRejectedException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

}