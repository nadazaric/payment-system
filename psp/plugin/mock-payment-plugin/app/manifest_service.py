import json
import logging
from pathlib import Path
from typing import Any

from app.config import MANIFEST_FILE_PATH, PLUGIN_BASE_URL
from app.log_strings import LogAction, LogFeature


def load_manifest() -> dict[str, Any]:
    manifest_path = Path(MANIFEST_FILE_PATH)

    with manifest_path.open(
            mode="r",
            encoding="utf-8"
    ) as manifest_file:
        manifest = json.load(manifest_file)

    return manifest


def find_payment_method_manifest(payment_method_code: str) -> dict[str, Any] | None:
    manifest = load_manifest()

    for method in manifest["methods"]:
        if method["code"] == payment_method_code:
            return method

    return None


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