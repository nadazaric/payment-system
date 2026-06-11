from urllib.parse import (
    parse_qsl,
    urlencode,
    urlparse,
    urlunparse
)

from fastapi import HTTPException

from app.psp_client import send_payment_callback_to_psp

FINAL_STATUSES = {
    "SUCCESS",
    "FAILED",
    "ERROR"
}


async def complete_mock_payment(
        session: dict,
        status: str
) -> str:
    validate_status(status)

    callback_delivered = await send_payment_callback_to_psp(
        session["pspCallbackUrl"],
        status,
        build_callback_message(status)
    )

    return build_final_redirect_url(
        session,
        status,
        callback_delivered
    )


def validate_status(status: str) -> None:
    if status in FINAL_STATUSES:
        return

    raise HTTPException(
        status_code=400,
        detail="Invalid mock payment result status."
    )


def build_callback_message(status: str) -> str:
    if status == "SUCCESS":
        return "Mock payment completed successfully."

    if status == "FAILED":
        return "Mock payment failed."

    return "Mock payment ended with error."


def build_final_redirect_url(
        session: dict,
        status: str,
        callback_delivered: bool
) -> str:
    if status == "SUCCESS":
        base_url = session["successUrl"]
    elif status == "FAILED":
        base_url = session["failUrl"]
    else:
        base_url = session["errorUrl"]

    return append_query_params(
        base_url,
        {
            "paymentId": session["paymentId"],
            "merchantOrderId": session["merchantOrderId"],
            "status": status,
            "callbackDelivered": str(callback_delivered).lower()
        }
    )


def append_query_params(
        url: str,
        params: dict[str, str]
) -> str:
    parsed_url = urlparse(url)

    query_params = dict(
        parse_qsl(parsed_url.query)
    )

    query_params.update(params)

    return urlunparse(
        parsed_url._replace(
            query=urlencode(query_params)
        )
    )