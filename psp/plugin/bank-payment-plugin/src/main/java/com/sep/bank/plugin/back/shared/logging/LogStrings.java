package com.sep.bank.plugin.back.shared.logging;

public final class LogStrings {

    private LogStrings() {
    }

    public static final class Feature {

        private Feature() {
        }

        public static final String APP = "APP";
        public static final String PLUGIN_SYNC = "PLUGIN_SYNC";
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
    }

    public static final class Reason {

        private Reason() {
        }

        public static final String PSP_SYNC_FAILED = "psp_sync_failed";
        public static final String MANIFEST_READ_FAILED = "manifest_read_failed";
        public static final String SIGNATURE_GENERATION_FAILED = "signature_generation_failed";
        public static final String JSON_SERIALIZATION_FAILED = "json_serialization_failed";
    }

}
