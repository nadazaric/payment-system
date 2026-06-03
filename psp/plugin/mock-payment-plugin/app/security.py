import base64
import hashlib
import hmac
import json
import logging
from datetime import datetime, timezone
from typing import Any

from app.config import PLUGIN_SECRET
from app.log_strings import LogAction, LogFeature, LogReason


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

    signature = base64.urlsafe_b64encode(signature_bytes) \
        .decode("utf-8") \
        .rstrip("=")

    return signature


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