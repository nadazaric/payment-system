from pydantic import BaseModel


class PluginConfigurationRequest(BaseModel):
    merchantId: str
    sellerReference: str
    paymentMethodCode: str
    values: dict[str, str]


class PluginConfigurationResponse(BaseModel):
    configured: bool
    message: str
