package com.sep.bank.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {

        private Feature() {
        }

        public static final String APP = "APP";
        public static final String PAYMENT = "PAYMENT";
    }

    public static final class Action {

        private Action() {
        }

        public static final String STARTED = "STARTED";
        public static final String STOPPED = "STOPPED";

        public static final String PAYMENT_CREATE_REQUEST_RECEIVED = "PAYMENT INIT REQ RECEIVED";
        public static final String PAYMENT_CREATE_REJECTED = "PAYMENT INIT REJECTED";
        public static final String PAYMENT_CREATED = "PAYMENT CREATED";
        public static final String PAYMENT_PAGE_NOT_FOUND = "PAYMENT PAGE NOT FOUND";
    }

    public static final class Reason {

        private Reason() {
        }

        public static final String BANK_MERCHANT_NOT_FOUND = "bank_merchant_not_found";
        public static final String PAYMENT_REQUEST_ALREADY_EXISTS = "payment_request_already_exists";
        public static final String SIGNATURE_GENERATION_FAILED = "signature_generation_failed";
        public static final String PAYMENT_NOT_FOUND = "payment_not_found";
    }

}