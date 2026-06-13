package com.sep.psp.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {
        public static final String APP = "APP";

        public static final String AUTH = "AUTH";
        public static final String MERCHANT = "MERCHANT";
        public static final String SELLER = "SELLER";
        public static final String PAYMENT_METHOD = "PAYMENT METHOD";
        public static final String PAYMENT_PLUGIN = "PAYMENT PLUGIN";
        public static final String PAYMENT = "PAYMENT";
        public static final String SCHEDULING = "SCHEDULING";

        private Feature() {
        }
    }

    public static final class Action {
        public static final String STARTED = "STARTED";
        public static final String STOPPED = "STOPPED";

        public static final String REGISTER_STARTED = "REGISTER STARTED";
        public static final String REGISTER_COMPLETED = "REGISTER COMPLETED";
        public static final String REGISTER_REJECTED = "REGISTER REJECTED";

        public static final String LOGIN_SUCCESS = "LOGIN SUCCESS";
        public static final String LOGIN_REJECTED = "LOGIN REJECTED";

        public static final String RABBITMQ_CONNECTED = "RABBITMQ CONNECTED";

        public static final String SELLER_CREATED = "SELLER CREATED";
        public static final String SELLER_UPDATED = "SELLER UPDATED";
        public static final String SELLER_CREATE_REJECTED = "SELLER CREATE REJECTED";
        public static final String SELLER_UPDATE_REJECTED = "SELLER UPDATE REJECTED";

        public static final String PAYMENT_METHOD_UPDATE_REJECTED = "PAYMENT METHOD UPDATE REJECTED";
        public static final String PAYMENT_METHOD_CONFIGURED = "PAYMENT METHOD CONFIGURED";
        public static final String PAYMENT_METHOD_REMOVED = "PAYMENT METHOD REMOVED";

        public static final String EXPECTED_PLUGIN_CREATED = "EXPECTED PLUGIN CREATED";
        public static final String EXPECTED_PLUGIN_CREATE_REJECTED = "EXPECTED PLUGIN CREATE REJECTED";
        public static final String PAYMENT_PLUGINS_LISTED = "PAYMENT PLUGINS LISTED";
        public static final String PLUGIN_SYNC_COMPLETED = "PLUGIN SYNC COMPLETED";
        public static final String PLUGIN_STATUS_UPDATED = "PLUGIN STATUS UPDATED";
        public static final String PLUGIN_STATUS_UPDATE_REJECTED = "PLUGIN STATUS UPDATE REJECTED";
        public static final String PLUGIN_HEALTH_CHECK_FAILED = "PLUGIN HEALTH CHECK FAILED";
        public static final String PLUGIN_REQUEST_FAILED = "PLUGIN REQUEST FAILED";

        public static final String PROFILE_UPDATED = "PROFILE UPDATED";
        public static final String ACTIVE_STATUS_CHANGED = "ACTIVE STATUS CHANGED";
        public static final String API_KEY_REGENERATED = "API KEY REGENERATED";

        public static final String PAYMENT_CREATED = "PAYMENT CREATED";
        public static final String PAYMENT_CREATE_REJECTED = "PAYMENT CREATE REJECTED";
        public static final String PAYMENT_INITIATED = "PAYMENT INITIATED";
        public static final String PAYMENT_INITIATE_REJECTED = "PAYMENT INITIATE REJECTED";
        public static final String PAYMENT_RESULT_PROCESSED = "PAYMENT RESULT PROCESSED";
        public static final String PAYMENT_RESULT_REJECTED = "PAYMENT RESULT REJECTED";
        public static final String PAYMENT_NOTIFICATION_PUBLISH_FAILED = "PAYMENT NOTIFICATION PUBLISH FAILED";

        public static final String PAYMENT_STATUS_CHECK_FAILED = "PAYMENT STATUS CHECK FAILED";
        public static final String PAYMENT_STATUS_CHECK_SCHEDULER_FAILED = "PAYMENT STATUS CHECK SCHEDULER FAILED";

        private Action() {
        }
    }

    public static final class Reason {

        public static final String USERNAME_TAKEN = "username_taken";
        public static final String SELLER_REFERENCE_TAKEN = "seller_reference_taken";
        public static final String OWNER_MISMATCH = "owner_mismatch";
        public static final String SELLER_NOT_FOUND = "seller_not_found";
        public static final String EMPTY_SELECTION = "empty_selection";
        public static final String UNKNOWN_PAYMENT_METHOD = "unknown_payment_method";
        public static final String INACTIVE_PAYMENT_METHOD = "inactive_payment_method";
        public static final String INACTIVE_PAYMENT_PLUGIN = "inactive_payment_plugin";
        public static final String PAYMENT_PLUGIN_EXISTS = "payment_plugin_exists";
        public static final String PAYMENT_PLUGIN_NOT_FOUND = "payment_plugin_not_found";

        public static final String MERCHANT_NOT_FOUND = "merchant_not_found";
        public static final String INVALID_MERCHANT_CREDENTIALS = "invalid_merchant_credentials";
        public static final String INACTIVE_MERCHANT = "inactive_merchant";
        public static final String INACTIVE_SELLER = "inactive_seller";
        public static final String INVALID_CURRENCY = "invalid_currency";
        public static final String DUPLICATE_MERCHANT_ORDER = "duplicate_merchant_order";
        public static final String PAYMENT_ALREADY_INITIATED = "payment_already_initiated";
        public static final String PAYMENT_METHOD_NOT_AVAILABLE = "payment_method_not_available";
        public static final String PLUGIN_REDIRECT_URL_MISSING = "plugin_redirect_url_missing";
        public static final String PAYMENT_NOT_INITIATED = "payment_not_initiated";
        public static final String PAYMENT_ALREADY_COMPLETED = "payment_already_completed";
        public static final String PAYMENT_PLUGIN_MISMATCH = "payment_plugin_mismatch";
        public static final String INVALID_PAYMENT_RESULT_STATUS = "invalid_payment_result_status";
        public static final String PAYMENT_METHOD_NOT_SELECTED = "payment_method_not_selected";
        public static final String SELECTED_PAYMENT_METHOD_NOT_AVAILABLE = "selected_payment_method_not_available";
        public static final String RABBITMQ_PUBLISH_FAILED = "rabbitmq_publish_failed";
        public static final String PAYMENT_STATUS_CHECK_FAILED = "payment_status_check_failed";

        private Reason() {
        }
    }
}