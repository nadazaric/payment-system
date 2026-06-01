import asyncio
import json
import logging
from contextlib import asynccontextmanager
from pathlib import Path
from typing import Any

import httpx
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

from config import (
    CONFIGURATIONS_FILE_PATH,
    MANIFEST_FILE_PATH,
    PLUGIN_BASE_URL,
    PSP_BASE_URL
)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s"
)


class PluginConfigurationRequest(BaseModel):
    merchantId: str
    sellerReference: str
    paymentMethodCode: str
    values: dict[str, str]


class PluginConfigurationResponse(BaseModel):
    configured: bool
    message: str


def load_manifest() -> dict[str, Any]:
    manifest_path = Path(MANIFEST_FILE_PATH)

    with manifest_path.open(
            mode="r",
            encoding="utf-8"
    ) as manifest_file:
        return json.load(manifest_file)


def load_configurations() -> dict[str, Any]:
    configurations_path = Path(CONFIGURATIONS_FILE_PATH)

    if not configurations_path.exists():
        return {}

    with configurations_path.open(
            mode="r",
            encoding="utf-8"
    ) as configurations_file:
        return json.load(configurations_file)


def save_configurations(configurations: dict[str, Any]) -> None:
    configurations_path = Path(CONFIGURATIONS_FILE_PATH)

    with configurations_path.open(
            mode="w",
            encoding="utf-8"
    ) as configurations_file:
        json.dump(
            configurations,
            configurations_file,
            indent=2
        )


def build_configuration_key(
        merchant_id: str,
        seller_reference: str,
        payment_method_code: str
) -> str:
    return f"{merchant_id}:{seller_reference}:{payment_method_code}"


def find_payment_method_manifest(payment_method_code: str) -> dict[str, Any] | None:
    manifest = load_manifest()

    for method in manifest["methods"]:
        if method["code"] == payment_method_code:
            return method

    return None


def validate_configuration_request(request: PluginConfigurationRequest) -> None:
    method_manifest = find_payment_method_manifest(request.paymentMethodCode)

    if method_manifest is None:
        raise HTTPException(
            status_code=400,
            detail="Payment method is not supported by this plugin."
        )

    config_fields = method_manifest.get(
        "configFields",
        []
    )

    for field in config_fields:
        field_name = field["fieldName"]

        if field_name not in request.values:
            raise HTTPException(
                status_code=400,
                detail=f"Missing configuration field: {field_name}"
            )

        if request.values[field_name] is None or request.values[field_name] == "":
            raise HTTPException(
                status_code=400,
                detail=f"Configuration field cannot be empty: {field_name}"
            )


def build_registration_request() -> dict[str, Any]:
    manifest = load_manifest()

    registration_request = {
        "pluginCode": manifest["pluginCode"],
        "displayName": manifest["displayName"],
        "baseUrl": PLUGIN_BASE_URL,
        "active": manifest.get(
            "active",
            True
        ),
        "methods": []
    }

    for method in manifest["methods"]:
        config_fields = method.get(
            "configFields",
            []
        )

        registration_request["methods"].append({
            "code": method["code"],
            "displayName": method["displayName"],
            "active": method.get(
                "active",
                True
            ),
            "updateRequired": method.get(
                "updateRequired",
                False
            ),
            "configSchemaJson": json.dumps(config_fields)
        })

    return registration_request


async def register_plugin_on_psp() -> None:
    register_url = f"{PSP_BASE_URL}/api/plugins/register"
    registration_request = build_registration_request()

    for attempt in range(
            1,
            6
    ):
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.post(
                    register_url,
                    json=registration_request
                )

                if response.status_code >= 400:
                    logging.warning(
                        "PSP rejected plugin registration. Status: %s, Body: %s",
                        response.status_code,
                        response.text
                    )

                response.raise_for_status()

                logging.info(
                    "Mock plugin registered on PSP. Response: %s",
                    response.text
                )

                return

        except httpx.HTTPError as exception:
            logging.warning(
                "Failed to register mock plugin on PSP. Attempt %s/5. Reason: %s",
                attempt,
                exception
            )

            await asyncio.sleep(2)

    logging.error(
        "Mock plugin could not be registered on PSP after all attempts."
    )


@asynccontextmanager
async def lifespan(app: FastAPI):
    await register_plugin_on_psp()
    yield


app = FastAPI(
    title="Mock Payment Plugin",
    description="Mock payment plugin used to test PSP pluggability.",
    version="1.0.0",
    lifespan=lifespan
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
    validate_configuration_request(request)

    configurations = load_configurations()

    configuration_key = build_configuration_key(
        request.merchantId,
        request.sellerReference,
        request.paymentMethodCode
    )

    configurations[configuration_key] = {
        "merchantId": request.merchantId,
        "sellerReference": request.sellerReference,
        "paymentMethodCode": request.paymentMethodCode,
        "values": request.values
    }

    save_configurations(configurations)

    return PluginConfigurationResponse(
        configured=True,
        message="Configuration saved successfully."
    )


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8085,
        reload=True
    )