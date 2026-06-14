import asyncio
import logging

import httpx

from app.config import PSP_BASE_URL
from app.log_strings import LogAction, LogFeature, LogReason
from app.manifest_service import build_sync_request, load_manifest
from app.models import PaymentPluginCallbackRequest
from app.security import (
    build_signed_headers,
    build_sync_headers,
    serialize_request_body
)

logger = logging.getLogger(__name__)


async def sync_plugin_on_psp() -> bool:
    try:
        sync_url = f"{PSP_BASE_URL}/api/plugins/sync"
        sync_request = build_sync_request()
        request_body = serialize_request_body(sync_request)

        headers = build_sync_headers(
            sync_request["pluginCode"],
            request_body
        )

    except RuntimeError as exception:
        logger.error(
            "%s action=%s reason=%s error=%s",
            LogFeature.PLUGIN_SYNC,
            LogAction.PSP_SYNC_FAILED,
            LogReason.PLUGIN_SECRET_MISSING,
            exception
        )

        return False

    except Exception as exception:
        logger.error(
            "%s action=%s reason=%s error=%s",
            LogFeature.PLUGIN_SYNC,
            LogAction.PSP_SYNC_FAILED,
            LogReason.PSP_REQUEST_FAILED,
            exception
        )

        return False

    last_error = None

    for attempt in range(
            1,
            6
    ):
        try:
            logger.info(
                "%s action=%s pluginCode=%s url=%s attempt=%s",
                LogFeature.PLUGIN_SYNC,
                LogAction.PSP_SYNC_REQUEST_SENT,
                sync_request["pluginCode"],
                sync_url,
                attempt
            )

            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.post(
                    sync_url,
                    content=request_body,
                    headers=headers
                )

                if response.status_code >= 400:
                    logger.warning(
                        "%s action=%s reason=%s status=%s body=%s attempt=%s",
                        LogFeature.PLUGIN_SYNC,
                        LogAction.PSP_SYNC_REJECTED,
                        LogReason.PSP_REQUEST_REJECTED,
                        response.status_code,
                        response.text,
                        attempt
                    )

                response.raise_for_status()

                logger.info(
                    "%s action=%s status=%s body=%s",
                    LogFeature.PLUGIN_SYNC,
                    LogAction.PSP_SYNC_COMPLETED,
                    response.status_code,
                    response.text
                )

                return True

        except httpx.HTTPError as exception:
            last_error = exception

            logger.warning(
                "%s action=%s reason=%s attempt=%s maxAttempts=%s error=%s",
                LogFeature.PLUGIN_SYNC,
                LogAction.PSP_SYNC_FAILED,
                LogReason.PSP_REQUEST_FAILED,
                attempt,
                5,
                exception
            )

            await asyncio.sleep(2)

    logger.error(
        "%s action=%s reason=%s error=%s",
        LogFeature.PLUGIN_SYNC,
        LogAction.PSP_SYNC_FAILED,
        LogReason.PSP_REQUEST_FAILED,
        last_error
    )

    return False


async def send_payment_callback_to_psp(
        callback_url: str,
        status: str,
        message: str
) -> bool:
    plugin_code = load_manifest()["pluginCode"]

    callback_request = PaymentPluginCallbackRequest(
        status=status,
        message=message
    )

    request_body = serialize_request_body(
        callback_request.model_dump(mode="json")
    )

    headers = build_signed_headers(
        plugin_code,
        request_body
    )

    last_error = None
    max_attempts = 3

    for attempt in range(
            1,
            max_attempts + 1
    ):
        try:
            logger.info(
                "%s action=%s url=%s status=%s attempt=%s",
                LogFeature.PLUGIN_PAYMENT,
                LogAction.PSP_PAYMENT_CALLBACK_REQUEST_SENT,
                callback_url,
                status,
                attempt
            )

            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.post(
                    callback_url,
                    content=request_body,
                    headers=headers
                )

                if response.status_code >= 400:
                    logger.warning(
                        "%s action=%s reason=%s status=%s body=%s attempt=%s",
                        LogFeature.PLUGIN_PAYMENT,
                        LogAction.PSP_PAYMENT_CALLBACK_REJECTED,
                        LogReason.PSP_REQUEST_REJECTED,
                        response.status_code,
                        response.text,
                        attempt
                    )

                response.raise_for_status()

                logger.info(
                    "%s action=%s callbackStatus=%s body=%s",
                    LogFeature.PLUGIN_PAYMENT,
                    LogAction.PSP_PAYMENT_CALLBACK_COMPLETED,
                    response.status_code,
                    response.text
                )

                return True

        except httpx.HTTPError as exception:
            last_error = exception

            logger.warning(
                "%s action=%s reason=%s attempt=%s maxAttempts=%s error=%s",
                LogFeature.PLUGIN_PAYMENT,
                LogAction.PSP_PAYMENT_CALLBACK_FAILED,
                LogReason.PSP_REQUEST_FAILED,
                attempt,
                max_attempts,
                exception
            )

            if attempt < max_attempts:
                await asyncio.sleep(2)

    logger.error(
        "%s action=%s reason=%s error=%s",
        LogFeature.PLUGIN_PAYMENT,
        LogAction.PSP_PAYMENT_CALLBACK_FAILED,
        LogReason.PSP_REQUEST_FAILED,
        last_error
    )

    return False