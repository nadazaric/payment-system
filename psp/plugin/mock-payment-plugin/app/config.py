import os

from dotenv import load_dotenv

load_dotenv()

PSP_BASE_URL = os.getenv(
    "PSP_BASE_URL",
    "http://localhost:8082"
)

PLUGIN_BASE_URL = os.getenv(
    "PLUGIN_BASE_URL",
    "http://localhost:8085"
)

PLUGIN_SECRET = os.getenv(
    "PLUGIN_SECRET",
    ""
)

MANIFEST_FILE_PATH = os.getenv(
    "MANIFEST_FILE_PATH",
    "manifest.json"
)

CONFIGURATIONS_FILE_PATH = os.getenv(
    "CONFIGURATIONS_FILE_PATH",
    "plugin-configurations.json"
)

PLUGIN_HOST = os.getenv(
    "PLUGIN_HOST",
    "0.0.0.0"
)

PLUGIN_PORT = int(os.getenv(
    "PLUGIN_PORT",
    "8085"
))

PLUGIN_SIGNATURE_MAX_TIMESTAMP_AGE_SECONDS = int(
    os.getenv(
        "PLUGIN_SIGNATURE_MAX_TIMESTAMP_AGE_SECONDS",
        "300"
    )
)
