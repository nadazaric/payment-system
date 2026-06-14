from urllib.parse import urlencode

from fastapi import HTTPException

from app.config import PLUGIN_BASE_URL
from app.configuration_service import (
    build_configuration_key,
    load_configurations
)
from app.manifest_service import find_payment_method_manifest
from app.models import (
    PluginPaymentInitiationRequest,
    PluginPaymentInitiationResponse
)
from app.payment_session_service import save_payment_session


def initiate_plugin_payment(
        request: PluginPaymentInitiationRequest
) -> PluginPaymentInitiationResponse:
    validate_payment_method_is_supported(request.paymentMethodCode)
    validate_seller_payment_method_is_configured(request)

    save_payment_session(request)

    redirect_url = build_mock_payment_redirect_url(request)

    return PluginPaymentInitiationResponse(
        redirectUrl=redirect_url
    )


def validate_payment_method_is_supported(
        payment_method_code: str
) -> None:
    method_manifest = find_payment_method_manifest(payment_method_code)

    if method_manifest is None:
        raise HTTPException(
            status_code=400,
            detail="Payment method is not supported by this plugin."
        )


def validate_seller_payment_method_is_configured(
        request: PluginPaymentInitiationRequest
) -> None:
    configurations = load_configurations()

    configuration_key = build_configuration_key(
        request.merchantId,
        request.sellerReference,
        request.paymentMethodCode
    )

    if configuration_key not in configurations:
        raise HTTPException(
            status_code=400,
            detail="Seller payment method is not configured on this plugin."
        )


def build_mock_payment_redirect_url(
        request: PluginPaymentInitiationRequest
) -> str:
    query_params = urlencode({
        "paymentId": request.paymentId
    })

    return f"{PLUGIN_BASE_URL}/mock-payment?{query_params}"