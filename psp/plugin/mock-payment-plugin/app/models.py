from decimal import Decimal

from pydantic import BaseModel


class PluginConfigurationRequest(BaseModel):
    merchantId: str
    sellerReference: str
    paymentMethodCode: str
    values: dict[str, str]


class PluginConfigurationResponse(BaseModel):
    configured: bool
    message: str


class PluginPaymentInitiationRequest(BaseModel):
    paymentId: str
    merchantId: str
    sellerReference: str
    paymentMethodCode: str
    amount: Decimal
    currency: str
    merchantOrderId: str
    successUrl: str
    failUrl: str
    errorUrl: str
    pspCallbackUrl: str


class PluginPaymentInitiationResponse(BaseModel):
    redirectUrl: str


class PaymentPluginCallbackRequest(BaseModel):
    status: str
    message: str | None = None