import asyncio
import base64
import hashlib
import hmac
import json
import logging
import sys
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

import httpx
import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

from config import (
    CONFIGURATIONS_FILE_PATH,
    MANIFEST_FILE_PATH,
    PLUGIN_BASE_URL,
    PLUGIN_SECRET,
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


def build_sync_request() -> dict[str, Any]:
    manifest = load_manifest()

    sync_request = {
        "pluginCode": manifest["pluginCode"],
        "displayName": manifest["displayName"],
        "baseUrl": PLUGIN_BASE_URL,
        "methods": []
    }

    for method in manifest["methods"]:
        config_fields = method.get(
            "configFields",
            []
        )

        sync_request["methods"].append({
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
            "configSchemaJson": json.dumps(
                config_fields,
                separators=(",", ":")
            )
        })

    return sync_request


def serialize_request_body(request_body: dict[str, Any]) -> str:
    return json.dumps(
        request_body,
        separators=(",", ":"),
        ensure_ascii=False
    )


def get_current_timestamp() -> str:
    return datetime.now(timezone.utc) \
        .isoformat() \
        .replace("+00:00", "Z")


def generate_signature(
        secret: str,
        timestamp: str,
        request_body: str
) -> str:
    payload = f"{timestamp}.{request_body}"

    signature_bytes = hmac.new(
        secret.encode("utf-8"),
        payload.encode("utf-8"),
        hashlib.sha256
    ).digest()

    return base64.urlsafe_b64encode(signature_bytes) \
        .decode("utf-8") \
        .rstrip("=")


def build_sync_headers(
        plugin_code: str,
        request_body: str
) -> dict[str, str]:
    if PLUGIN_SECRET is None or PLUGIN_SECRET == "":
        raise RuntimeError("PLUGIN_SECRET is not configured.")

    timestamp = get_current_timestamp()

    signature = generate_signature(
        PLUGIN_SECRET,
        timestamp,
        request_body
    )

    return {
        "Content-Type": "application/json",
        "X-Plugin-Code": plugin_code,
        "X-Timestamp": timestamp,
        "X-Signature": signature
    }


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
        logging.error(
            "Mock plugin sync configuration error: %s",
            exception
        )

        return False
    except Exception as exception:
        logging.error(
            "Mock plugin could not build sync request: %s",
            exception
        )

        return False

    last_error = None

    for attempt in range(
            1,
            6
    ):
        try:
            async with httpx.AsyncClient(timeout=5.0) as client:
                response = await client.post(
                    sync_url,
                    content=request_body,
                    headers=headers
                )

                if response.status_code >= 400:
                    logging.warning(
                        "PSP rejected plugin sync. Status: %s, Body: %s",
                        response.status_code,
                        response.text
                    )

                response.raise_for_status()

                logging.info(
                    "Mock plugin synchronized with PSP. Response: %s",
                    response.text
                )

                return True

        except httpx.HTTPError as exception:
            last_error = exception

            logging.warning(
                "Failed to synchronize mock plugin with PSP. Attempt %s/5. Reason: %s",
                attempt,
                exception
            )

            await asyncio.sleep(2)

    logging.error(
        "Mock plugin could not be synchronized with PSP after all attempts. Plugin will not start. Last error: %s",
        last_error
    )

    return False


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
    synchronized = asyncio.run(sync_plugin_on_psp())

    if not synchronized:
        logging.error(
            "Mock plugin stopped because synchronization with PSP failed."
        )

        sys.exit(1)

    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8085,
        reload=False
    )