class LogFeature:
    PLUGIN_SYNC = "plugin_sync"
    PLUGIN_CONFIGURATION = "plugin_configuration"
    PLUGIN_PAYMENT = "plugin_payment"
    PLUGIN_SERVER = "plugin_server"


class LogAction:
    PSP_SYNC_REQUEST_SENT = "psp_sync_request_sent"
    PSP_SYNC_COMPLETED = "psp_sync_completed"
    PSP_SYNC_REJECTED = "psp_sync_rejected"
    PSP_SYNC_FAILED = "psp_sync_failed"

    CONFIGURATION_REQUEST_RECEIVED = "configuration_request_received"
    CONFIGURATION_SAVED = "configuration_saved"
    CONFIGURATION_REJECTED = "configuration_rejected"

    PAYMENT_INITIATE_REQUEST_RECEIVED = "payment_initiate_request_received"
    PAYMENT_INITIATED = "payment_initiated"
    PAYMENT_INITIATE_REJECTED = "payment_initiate_rejected"

    PAYMENT_COMPLETION_REQUEST_RECEIVED = "payment_completion_request_received"
    PAYMENT_COMPLETED = "payment_completed"
    PAYMENT_COMPLETION_REJECTED = "payment_completion_rejected"

    PSP_PAYMENT_CALLBACK_REQUEST_SENT = "psp_payment_callback_request_sent"
    PSP_PAYMENT_CALLBACK_COMPLETED = "psp_payment_callback_completed"
    PSP_PAYMENT_CALLBACK_REJECTED = "psp_payment_callback_rejected"
    PSP_PAYMENT_CALLBACK_FAILED = "psp_payment_callback_failed"

    SERVER_STARTED = "==================== SERVER STARTED ===================="
    SERVER_STOPPED = "==================== SERVER STOPPED ===================="


class LogReason:
    PSP_REQUEST_REJECTED = "psp_request_rejected"
    PSP_REQUEST_FAILED = "psp_request_failed"
    PLUGIN_SECRET_MISSING = "plugin_secret_missing"
    CONFIGURATION_INVALID = "configuration_invalid"
    CONFIGURATION_NOT_FOUND = "configuration_not_found"
    PAYMENT_METHOD_NOT_SUPPORTED = "payment_method_not_supported"
    PAYMENT_SESSION_NOT_FOUND = "payment_session_not_found"
    INVALID_PAYMENT_RESULT_STATUS = "invalid_payment_result_status"
    SYNCHRONIZATION_WITH_PSP_FAILED = "synchronization_with_psp_failed"