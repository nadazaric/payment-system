import json
import logging

from fastapi import FastAPI, HTTPException, Request
from pydantic import ValidationError

from app.configuration_service import save_plugin_configuration
from app.log_strings import LogAction, LogFeature, LogReason
from app.manifest_service import load_manifest
from app.models import PluginConfigurationRequest, PluginConfigurationResponse
from app.security import verify_signed_request

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
async def save_configuration(
        http_request: Request
) -> PluginConfigurationResponse:
    try:
        request_body = await verify_signed_request(http_request)

        request_data = json.loads(request_body)

        request = PluginConfigurationRequest(
            **request_data
        )

        logger.info(
            "%s action=%s merchantId=%s sellerReference=%s paymentMethodCode=%s",
            LogFeature.PLUGIN_CONFIGURATION,
            LogAction.CONFIGURATION_REQUEST_RECEIVED,
            request.merchantId,
            request.sellerReference,
            request.paymentMethodCode
        )

        save_plugin_configuration(request)

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

    except HTTPException as exception:
        logger.warning(
            "%s action=%s reason=%s status=%s detail=%s",
            LogFeature.PLUGIN_CONFIGURATION,
            LogAction.CONFIGURATION_REJECTED,
            LogReason.CONFIGURATION_INVALID,
            exception.status_code,
            exception.detail
        )

        raise exception

    except (
            json.JSONDecodeError,
            ValidationError
    ) as exception:
        logger.warning(
            "%s action=%s reason=%s error=%s",
            LogFeature.PLUGIN_CONFIGURATION,
            LogAction.CONFIGURATION_REJECTED,
            LogReason.CONFIGURATION_INVALID,
            exception
        )

        raise HTTPException(
            status_code=400,
            detail="Invalid configuration request body."
        )