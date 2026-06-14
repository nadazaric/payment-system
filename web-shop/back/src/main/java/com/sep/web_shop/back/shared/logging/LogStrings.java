package com.sep.web_shop.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {

        private Feature() {
        }

        public static final String PAYMENT = "PAYMENT";
        public static final String AUTH = "AUTH";
        public static final String APP = "APP";
    }

    public static final class Action {

        private Action() {
        }

        public static final String STARTED = "STARTED";
        public static final String STOPPED = "STOPPED";

        public static final String PAYMENT_REQUEST_RECEIVED = "PAYMENT REQUEST RECEIVED";
        public static final String PAYMENT_CREATE_REQUEST_SENT = "PAYMENT CREATE REQUEST SENT";
        public static final String PAYMENT_CREATE_REJECTED = "PAYMENT CREATE REJECTED";
        public static final String RESERVATION_PAYMENT_INITIATED = "RESERVATION PAYMENT INITIATED";

        public static final String PAYMENT_NOTIFICATION_RECEIVED = "PAYMENT NOTIFICATION RECEIVED";
        public static final String PAYMENT_NOTIFICATION_PROCESSED = "PAYMENT NOTIFICATION PROCESSED";
        public static final String PAYMENT_NOTIFICATION_REJECTED = "PAYMENT NOTIFICATION REJECTED";

        public static final String USER_LOGGED_IN = "USER LOGGED IN";
        public static final String USER_LOGIN_REJECTED = "USER LOGIN REJECTED";

        public static final String RABBITMQ_CONNECTED = "RABBITMQ CONNECTED";
    }

    public static final class Reason {

        private Reason() {
        }

        public static final String INVALID_RESERVATION_PERIOD = "invalid_reservation_period";
        public static final String VEHICLE_UNAVAILABLE = "vehicle_unavailable";
        public static final String ADDITIONAL_SERVICE_NOT_FOUND = "additional_service_not_found";
        public static final String PSP_PAYMENT_CREATE_FAILED = "psp_payment_create_failed";

        public static final String PAYMENT_NOTIFICATION_MERCHANT_MISMATCH = "payment_notification_merchant_mismatch";
        public static final String PAYMENT_NOTIFICATION_SELLER_MISMATCH = "payment_notification_seller_mismatch";
        public static final String PAYMENT_NOTIFICATION_INVALID_STATUS = "payment_notification_invalid_status";
        public static final String RESERVATION_PAYMENT_NOT_FOUND = "reservation_payment_not_found";
        public static final String RESERVATION_PAYMENT_ALREADY_COMPLETED = "reservation_payment_already_completed";
        public static final String MISSING_REQUIRED_RESERVATION_DATA = "missing_required_reservation_data";
        public static final String PAYMENT_NOTIFICATION_AMOUNT_MISMATCH = "payment_notification_amount_mismatch";
        public static final String PAYMENT_NOTIFICATION_CURRENCY_MISMATCH = "payment_notification_currency_mismatch";

        public static final String INVALID_CREDENTIALS = "invalid_credentials";
    }

}