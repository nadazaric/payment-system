import json
from pathlib import Path

from fastapi import HTTPException

from app.config import PAYMENT_SESSIONS_FILE_PATH
from app.models import PluginPaymentInitiationRequest


def save_payment_session(request: PluginPaymentInitiationRequest) -> None:
    sessions = load_payment_sessions()

    sessions[request.paymentId] = request.model_dump(mode="json")

    write_payment_sessions(sessions)


def get_payment_session(payment_id: str) -> dict:
    sessions = load_payment_sessions()

    session = sessions.get(payment_id)

    if session is None:
        raise HTTPException(
            status_code=404,
            detail="Mock payment session not found."
        )

    return session


def load_payment_sessions() -> dict:
    file_path = Path(PAYMENT_SESSIONS_FILE_PATH)

    if not file_path.exists():
        return {}

    with file_path.open(
            "r",
            encoding="utf-8"
    ) as file:
        return json.load(file)


def write_payment_sessions(sessions: dict) -> None:
    file_path = Path(PAYMENT_SESSIONS_FILE_PATH)

    with file_path.open(
            "w",
            encoding="utf-8"
    ) as file:
        json.dump(
            sessions,
            file,
            indent=2,
            ensure_ascii=False
        )