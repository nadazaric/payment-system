package com.sep.bank.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {

        private Feature() {
        }

        public static final String APP = "APP";
        public static final String PAYMENT = "PAYMENT";
        public static final String SCHEDULING = "SCHEDULING";
    }

    public static final class Action {

        private Action() {
        }

        public static final String STARTED = "STARTED";
        public static final String STOPPED = "STOPPED";
        public static final String REQUEST_REJECTED = "REQUEST REJECTED";

        public static final String PAYMENT_CREATE_REQUEST_RECEIVED = "PAYMENT INIT REQ RECEIVED";
        public static final String PAYMENT_CREATE_REJECTED = "PAYMENT INIT REJECTED";
        public static final String PAYMENT_CREATED = "PAYMENT CREATED";
        public static final String PAYMENT_PAGE_NOT_FOUND = "PAYMENT PAGE NOT FOUND";

        public static final String CARD_PAYMENT_SUBMIT_RECEIVED = "CARD PAYMENT SUBMIT RECEIVED";
        public static final String CARD_PAYMENT_REJECTED = "PAYMENT REJECTED";
        public static final String CARD_PAYMENT_COMPLETED = "CARD PAYMENT COMPLETED";
        public static final String PLUGIN_CALLBACK_RETRY = "PLUGIN CALLBACK RETRY";
        public static final String PLUGIN_CALLBACK_FAILED = "PLUGIN CALLBACK FAILED";
        public static final String PAYMENT_EXPIRED = "PAYMENT EXPIRED";
        public static final String EXPIRED_PAYMENT_CHECK_FAILED = "EXPIRED PAYMENT CHECK FAILED";
        public static final String PAYMENT_STATUS_CHECK_REJECTED = "PAYMENT STATUS CHECK REJECTED";

        public static final String QR_SCAN_REJECTED = "QR SCAN REJECTED";
        public static final String QR_PAYMENT_SUBMIT_RECEIVED = "QR PAYMENT SUBMIT RECEIVED";
        public static final String QR_PAYMENT_COMPLETED = "QR PAYMENT COMPLETED";
        public static final String QR_PAYMENT_REJECTED = "QR PAYMENT REJECTED";
        public static final String QR_GENERATION_REJECTED = "QR GENERATION REJECTED";

    }

    public static final class Reason {

        private Reason() {
        }

        public static final String BANK_MERCHANT_NOT_FOUND = "bank_merchant_not_found";
        public static final String PAYMENT_REQUEST_ALREADY_EXISTS = "payment_request_already_exists";
        public static final String SIGNATURE_GENERATION_FAILED = "signature_generation_failed";
        public static final String PAYMENT_NOT_FOUND = "payment_not_found";
        public static final String PAYMENT_NOT_AVAILABLE_FOR_PROCESSING = "payment_not_available_for_processing";
        public static final String PAYMENT_EXPIRED = "payment_expired";
        public static final String INVALID_PAYMENT_METHOD = "invalid_payment_method";
        public static final String INVALID_PAN = "invalid_pan";
        public static final String PAN_LUHN_VALIDATION_FAILED = "pan_luhn_validation_failed";
        public static final String CARD_NOT_FOUND = "card_not_found";
        public static final String INVALID_SECURITY_CODE = "invalid_security_code";
        public static final String INVALID_CARD_HOLDER_NAME = "invalid_card_holder_name";
        public static final String INVALID_EXPIRATION_DATE_FORMAT = "invalid_expiration_date_format";
        public static final String CARD_EXPIRATION_DATE_MISMATCH = "card_expiration_date_mismatch";
        public static final String CARD_EXPIRED = "card_expired";
        public static final String PAYMENT_CARD_INACTIVE = "payment_card_inactive";
        public static final String BANK_ACCOUNT_INACTIVE = "bank_account_inactive";
        public static final String MERCHANT_NOT_FOUND = "merchant_not_found";
        public static final String CURRENCY_MISMATCH = "currency_mismatch";
        public static final String INSUFFICIENT_FUNDS = "insufficient_funds";
        public static final String CARD_PAYMENT_PROCESSING_ERROR = "card_payment_processing_error";
        public static final String PLUGIN_CALLBACK_FAILED = "max_num_of_attempts_reached";
        public static final String INVALID_QR_PAYLOAD = "invalid_qr_payload";
        public static final String QR_PAYMENT_NOT_FOUND = "qr_payment_not_found";
        public static final String QR_PAYMENT_AMOUNT_MISMATCH = "qr_payment_amount_mismatch";
        public static final String QR_PAYMENT_RECIPIENT_ACCOUNT_MISMATCH = "qr_payment_recipient_account_mismatch";
        public static final String PAYER_ACCOUNT_NOT_FOUND = "payer_account_not_found";
        public static final String QR_PAYMENT_PROCESSING_ERROR = "qr_payment_processing_error";
        public static final String PAYER_ACCOUNT_NOT_ALLOWED = "payer_account_not_allowed";
        public static final String QR_PAYMENT_CONTENT_GENERATION_FAILED = "qr_payment_content_generation_failed";

    }

}