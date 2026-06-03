import logging

from fastapi import FastAPI, HTTPException

from app.configuration_service import save_plugin_configuration
from app.log_strings import LogAction, LogFeature, LogReason
from app.manifest_service import load_manifest
from app.models import PluginConfigurationRequest, PluginConfigurationResponse

logger = logging.getLogger(__name__)

app = FastAPI(
    title="Mock Payment Plugin",
    description="Mock payment plugin used to test PSP pluggability.",
    version="1.0.0"
)


@app.get("/api/plugin/health")
def health():
    manifest = load_manifest()

    return {
        "status": "UP",
        "pluginCode": manifest["pluginCode"]
    }


@app.post("/api/plugin/configurations")
def save_configuration(
        request: PluginConfigurationRequest
) -> PluginConfigurationResponse:
    logger.info(
        "%s action=%s merchantId=%s sellerReference=%s paymentMethodCode=%s",
        LogFeature.PLUGIN_CONFIGURATION,
        LogAction.CONFIGURATION_REQUEST_RECEIVED,
        request.merchantId,
        request.sellerReference,
        request.paymentMethodCode
    )

    try:
        save_plugin_configuration(request)
    except HTTPException as exception:
        logger.warning(
            "%s action=%s reason=%s merchantId=%s sellerReference=%s paymentMethodCode=%s status=%s detail=%s",
            LogFeature.PLUGIN_CONFIGURATION,
            LogAction.CONFIGURATION_REJECTED,
            LogReason.CONFIGURATION_INVALID,
            request.merchantId,
            request.sellerReference,
            request.paymentMethodCode,
            exception.status_code,
            exception.detail
        )

        raise exception

    logger.info(
        "%s action=%s merchantId=%s sellerReference=%s paymentMethodCode=%s",
        LogFeature.PLUGIN_CONFIGURATION,
        LogAction.CONFIGURATION_SAVED,
        request.merchantId,
        request.sellerReference,
        request.paymentMethodCode
    )

    return PluginConfigurationResponse(
        configured=True,
        message="Configuration saved successfully."
    )