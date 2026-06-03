import json
import logging
from pathlib import Path
from typing import Any

from fastapi import HTTPException

from app.config import CONFIGURATIONS_FILE_PATH
from app.log_strings import LogAction, LogFeature, LogReason
from app.manifest_service import find_payment_method_manifest
from app.models import PluginConfigurationRequest


def load_configurations() -> dict[str, Any]:
    configurations_path = Path(CONFIGURATIONS_FILE_PATH)

    if not configurations_path.exists():
        return {}

    with configurations_path.open(
            mode="r",
            encoding="utf-8"
    ) as configurations_file:
        configurations = json.load(configurations_file)

    return configurations


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


def save_plugin_configuration(request: PluginConfigurationRequest) -> None:
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