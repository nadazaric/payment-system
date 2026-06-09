import json
import logging

from fastapi import FastAPI, HTTPException, Request, Response
from fastapi.responses import HTMLResponse
from pydantic import ValidationError

from app.configuration_service import save_plugin_configuration
from app.log_strings import LogAction, LogFeature, LogReason
from app.manifest_service import load_manifest
from app.models import (
    PluginConfigurationRequest,
    PluginConfigurationResponse,
    PluginPaymentInitiationRequest,
    PluginPaymentInitiationResponse
)
from app.payment_initiation_service import initiate_plugin_payment
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


@app.post("/api/plugin/payments/initiate")
async def initiate_payment(
        http_request: Request
) -> PluginPaymentInitiationResponse:
    try:
        request_body = await verify_signed_request(http_request)

        request_data = json.loads(request_body)

        request = PluginPaymentInitiationRequest(
            **request_data
        )

        logger.info(
            "%s action=%s paymentId=%s merchantId=%s sellerReference=%s paymentMethodCode=%s amount=%s currency=%s",
            LogFeature.PLUGIN_PAYMENT,
            LogAction.PAYMENT_INITIATE_REQUEST_RECEIVED,
            request.paymentId,
            request.merchantId,
            request.sellerReference,
            request.paymentMethodCode,
            request.amount,
            request.currency
        )

        response = initiate_plugin_payment(request)

        logger.info(
            "%s action=%s paymentId=%s pluginPaymentId=%s redirectUrl=%s",
            LogFeature.PLUGIN_PAYMENT,
            LogAction.PAYMENT_INITIATED,
            request.paymentId,
            response.pluginPaymentId,
            response.redirectUrl
        )

        return response

    except HTTPException as exception:
        logger.warning(
            "%s action=%s reason=%s status=%s detail=%s",
            LogFeature.PLUGIN_PAYMENT,
            LogAction.PAYMENT_INITIATE_REJECTED,
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
            LogFeature.PLUGIN_PAYMENT,
            LogAction.PAYMENT_INITIATE_REJECTED,
            LogReason.CONFIGURATION_INVALID,
            exception
        )

        raise HTTPException(
            status_code=400,
            detail="Invalid payment initiation request body."
        )


@app.get("/mock-payment")
def mock_payment_page(
        paymentId: str,
        pluginPaymentId: str,
        paymentMethodCode: str,
        amount: str,
        currency: str
) -> HTMLResponse:
    html = f"""
    <!DOCTYPE html>
    <html>
        <head>
            <title>Mock Payment</title>
            <style>
                body {{
                    font-family: Arial, sans-serif;
                    background: #f5f5f7;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    min-height: 100vh;
                    margin: 0;
                }}

                .card {{
                    background: white;
                    border-radius: 18px;
                    padding: 32px;
                    width: 420px;
                    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.10);
                }}

                h1 {{
                    margin-top: 0;
                    font-size: 26px;
                }}

                .row {{
                    margin-bottom: 12px;
                }}

                .label {{
                    color: #666;
                    font-size: 13px;
                    margin-bottom: 4px;
                }}

                .value {{
                    font-weight: 700;
                }}

                .note {{
                    margin-top: 24px;
                    color: #777;
                    font-size: 14px;
                    line-height: 1.5;
                }}
            </style>
        </head>
        <body>
            <div class="card">
                <h1>Mock payment page</h1>

                <div class="row">
                    <div class="label">PSP payment ID</div>
                    <div class="value">{paymentId}</div>
                </div>

                <div class="row">
                    <div class="label">Plugin payment ID</div>
                    <div class="value">{pluginPaymentId}</div>
                </div>

                <div class="row">
                    <div class="label">Payment method</div>
                    <div class="value">{paymentMethodCode}</div>
                </div>

                <div class="row">
                    <div class="label">Amount</div>
                    <div class="value">{amount} {currency}</div>
                </div>

                <div class="note">
                    This page is only a mock payment page.
                </div>
            </div>
        </body>
    </html>
    """

    return HTMLResponse(content=html)


@app.post("/api/plugin/heartbeat")
async def heartbeat(
        http_request: Request
) -> Response:
    await verify_signed_request(http_request)

    return Response(status_code=204)