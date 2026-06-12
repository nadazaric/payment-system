package com.sep.bank.back.shared.exception;

public class CardPaymentRejectedException extends RuntimeException {

    public CardPaymentRejectedException(String message) {
        super(message);
    }

}