package com.sep.bank.plugin.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {

        private Feature() {
        }

        public static final String APP = "APP";
        public static final String PLUGIN_SYNC = "PLUGIN_SYNC";
        public static final String PLUGIN_CONFIGURATION = "PLUGIN_CONFIG";
        public static final String PAYMENT = "PAYMENT";
    }

    public static final class Action {

        private Action() {
        }

        public static final String STARTED = "STARTED";
        public static final String STOPPED = "STOPPED";
        public static final String HEARTBEAT = "HEARTBEAT";

        public static final String PSP_SYNC_REQUEST_SENT = "PSP SYNC REQUEST SENT";
        public static final String PSP_SYNC_COMPLETED = "PSP SYNC COMPLETED";
        public static final String PSP_SYNC_FAILED = "PSP SYNC FAILED";
        public static final String PSP_SYNC_RETRY = "PSP SYNC RETRY";

        public static final String CONFIGURATION_REQUEST_RECEIVED = "CONFIGURATION REQUEST RECEIVED";
        public static final String CONFIGURATION_SAVED = "CONFIGURATION SAVED";
        public static final String CONFIGURATION_REJECTED = "CONFIGURATION REJECTED";

        public static final String PAYMENT_INITIATE_REQUEST_RECEIVED = "INIT REQUEST RECEIVED";
        public static final String PAYMENT_INITIATE_REJECTED = "INIT REJECTED";
        public static final String PAYMENT_INITIATE_COMPLETED = "INIT COMPLETED";
        public static final String PAYMENT_INITIATE_ALREADY_EXISTS = "INIT ALREADY EXISTS";
        public static final String BANK_PAYMENT_INITIATE_REJECTED = "BANK PAYMENT INIT REJECTED";
    }

    public static final class Reason {

        private Reason() {
        }

        public static final String PSP_SYNC_FAILED = "psp_sync_failed";
        public static final String MANIFEST_READ_FAILED = "manifest_read_failed";
        public static final String SIGNATURE_GENERATION_FAILED = "signature_generation_failed";
        public static final String JSON_SERIALIZATION_FAILED = "json_serialization_failed";

        public static final String MISSING_BANK_MERCHANT_ID = "missing_bank_merchant_id";
        public static final String INVALID_PAYMENT_METHOD_CODE = "invalid_payment_method_code";

        public static final String SELLER_PAYMENT_CONFIGURATION_NOT_FOUND = "seller_payment_configuration_not_found";
        public static final String BANK_PAYMENT_CREATE_FAILED = "bank_payment_create_failed";
    }

}
