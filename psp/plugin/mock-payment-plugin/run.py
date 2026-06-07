import asyncio
import logging
import sys

import uvicorn

from app.config import PLUGIN_HOST, PLUGIN_PORT
from app.log_strings import LogAction, LogFeature, LogReason
from app.psp_client import sync_plugin_on_psp

logging.basicConfig(
        level=logging.INFO,
        format="%(asctime)s [%(levelname)s] %(message)s",
        handlers=[
            logging.StreamHandler(),
            logging.FileHandler(
                "plugin.log",
                encoding="utf-8"
            )
        ]
    )

logger = logging.getLogger(__name__)


if __name__ == "__main__":
    logger.info(LogAction.SERVER_STARTED)

    synchronized = asyncio.run(sync_plugin_on_psp())

    if not synchronized:
        logger.error(LogAction.SERVER_STOPPED)

        sys.exit(1)

    uvicorn.run(
        "app.main:app",
        host=PLUGIN_HOST,
        port=PLUGIN_PORT,
        reload=False
    )