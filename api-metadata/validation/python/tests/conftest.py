"""Shared fixtures and utilities for DBC API tests."""
import os
import pytest


def _load_env_file(path):
    """Parse simple KEY=VALUE lines from an env file."""
    env = {}
    try:
        with open(path) as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith("#"):
                    continue
                if "=" in line:
                    key, val = line.split("=", 1)
                    env[key.strip()] = val.strip().strip('"').strip("'")
    except FileNotFoundError:
        pass
    return env


@pytest.fixture(scope="session")
def dbc_credentials():
    """Load DBC test credentials from environment or .env file.
    
    Priority:
    1. Environment variables (DBC_USERNAME, DBC_PASSWORD)
    2. .env file in repository root
    3. Fallback defaults (may not work in CI/prod)
    """
    username = os.environ.get("DBC_USERNAME")
    password = os.environ.get("DBC_PASSWORD")
    
    if not username or not password:
        # Try loading from a .env file (repo root preferred, fallback to tests dir).
        # original layout placed tests at top level; after restructuring we
        # search two levels up which corresponds to the repository root.
        repo_env = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", ".env"))
        test_env = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".env"))
        for env_path in (repo_env, test_env):
            env_vars = _load_env_file(env_path)
            username = username or env_vars.get("USERNAME")
            password = password or env_vars.get("PASSWORD")
            if username and password:
                break
    
    if not username or not password:
        pytest.skip("DBC_USERNAME and DBC_PASSWORD not configured")
    
    return {"username": username, "password": password}


@pytest.fixture(scope="session")
def sample_images_dir():
    """Path to sample images used in tests."""
    return os.path.join(os.path.dirname(__file__), "..", "samples")


@pytest.fixture(scope="session")
def sample_image(sample_images_dir):
    """Path to normal.jpg sample image."""
    path = os.path.join(sample_images_dir, "normal.jpg")
    if not os.path.isfile(path):
        pytest.skip("Sample image not found")
    return path


@pytest.fixture(scope="session")
def banner_image(sample_images_dir):
    """Path to banner.jpg sample image."""
    path = os.path.join(sample_images_dir, "banner.jpg")
    if not os.path.isfile(path):
        pytest.skip("Banner image not found")
    return path


@pytest.fixture(scope="session")
def audio_file(sample_images_dir):
    """Path to audio.mp3 sample file."""
    path = os.path.join(sample_images_dir, "audio.mp3")
    if not os.path.isfile(path):
        pytest.skip("Audio file not found")
    return path
