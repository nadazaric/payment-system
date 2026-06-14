import base64
import hashlib
import hmac
import json
from datetime import datetime, timezone
from typing import Any

from fastapi import HTTPException, Request

from app.config import (
    PLUGIN_SECRET,
    PLUGIN_SIGNATURE_MAX_TIMESTAMP_AGE_SECONDS
)
from app.manifest_service import load_manifest


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


def build_signed_headers(
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


def build_sync_headers(
        plugin_code: str,
        request_body: str
) -> dict[str, str]:
    return build_signed_headers(
        plugin_code,
        request_body
    )


async def verify_signed_request(request: Request) -> str:
    if PLUGIN_SECRET is None or PLUGIN_SECRET == "":
        raise HTTPException(
            status_code=500,
            detail="PLUGIN_SECRET is not configured."
        )

    request_body_bytes = await request.body()
    request_body = request_body_bytes.decode("utf-8")

    plugin_code_header = request.headers.get("X-Plugin-Code")
    timestamp = request.headers.get("X-Timestamp")
    signature = request.headers.get("X-Signature")

    validate_required_headers(
        plugin_code_header,
        timestamp,
        signature
    )

    validate_plugin_code(plugin_code_header)
    validate_timestamp(timestamp)

    expected_signature = generate_signature(
        PLUGIN_SECRET,
        timestamp,
        request_body
    )

    if not hmac.compare_digest(
            expected_signature,
            signature
    ):
        raise HTTPException(
            status_code=401,
            detail="Invalid PSP request signature."
        )

    return request_body


def validate_required_headers(
        plugin_code_header: str | None,
        timestamp: str | None,
        signature: str | None
) -> None:
    if plugin_code_header is None or plugin_code_header == "":
        raise HTTPException(
            status_code=401,
            detail="Plugin code header is required."
        )

    if timestamp is None or timestamp == "":
        raise HTTPException(
            status_code=401,
            detail="Timestamp header is required."
        )

    if signature is None or signature == "":
        raise HTTPException(
            status_code=401,
            detail="Signature header is required."
        )


def validate_plugin_code(plugin_code_header: str) -> None:
    manifest = load_manifest()
    expected_plugin_code = manifest["pluginCode"]

    if plugin_code_header != expected_plugin_code:
        raise HTTPException(
            status_code=401,
            detail="Plugin code header is not valid."
        )


def validate_timestamp(timestamp: str) -> None:
    try:
        normalized_timestamp = timestamp.replace(
            "Z",
            "+00:00"
        )

        request_time = datetime.fromisoformat(normalized_timestamp)

        if request_time.tzinfo is None:
            request_time = request_time.replace(tzinfo=timezone.utc)

        now = datetime.now(timezone.utc)

        age_seconds = abs(
            (
                    now - request_time
            ).total_seconds()
        )

        if age_seconds > PLUGIN_SIGNATURE_MAX_TIMESTAMP_AGE_SECONDS:
            raise HTTPException(
                status_code=401,
                detail="PSP request timestamp is not valid."
            )
    except HTTPException:
        raise
    except Exception:
        raise HTTPException(
            status_code=401,
            detail="PSP request timestamp is not valid."
        )