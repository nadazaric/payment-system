class LogFeature:
    PLUGIN_SYNC = "plugin_sync"
    PLUGIN_CONFIGURATION = "plugin_configuration"
    PLUGIN_SERVER = "plugin_server"


class LogAction:
    PSP_SYNC_REQUEST_SENT = "psp_sync_request_sent"
    PSP_SYNC_COMPLETED = "psp_sync_completed"
    PSP_SYNC_REJECTED = "psp_sync_rejected"
    PSP_SYNC_FAILED = "psp_sync_failed"

    CONFIGURATION_REQUEST_RECEIVED = "configuration_request_received"
    CONFIGURATION_SAVED = "configuration_saved"
    CONFIGURATION_REJECTED = "configuration_rejected"

    SERVER_STARTED = "==================== SERVER STARTED ===================="
    SERVER_STOPPED = "==================== SERVER STOPPED ===================="


class LogReason:
    PSP_REQUEST_REJECTED = "psp_request_rejected"
    PSP_REQUEST_FAILED = "psp_request_failed"
    PLUGIN_SECRET_MISSING = "plugin_secret_missing"
    CONFIGURATION_INVALID = "configuration_invalid"
    SYNCHRONIZATION_WITH_PSP_FAILED = "synchronization_with_psp_failed"