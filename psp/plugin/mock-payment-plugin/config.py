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