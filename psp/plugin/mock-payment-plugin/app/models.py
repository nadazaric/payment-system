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


class PluginPaymentInitiationResponse(BaseModel):
    pluginPaymentId: str
    redirectUrl: str