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

        public static final String SELLER_CREATED = "SELLER CREATED";
        public static final String SELLER_UPDATED = "SELLER UPDATED";
        public static final String SELLER_CREATE_REJECTED = "SELLER CREATE REJECTED";
        public static final String SELLER_UPDATE_REJECTED = "SELLER UPDATE REJECTED";

        public static final String PAYMENT_METHODS_UPDATED = "PAYMENT METHODS UPDATED";
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

        private Reason() {
        }
    }
}