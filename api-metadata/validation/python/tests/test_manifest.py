"""Verify that the JSON-LD endpoint manifest matches the OpenAPI spec."""
import json
from pathlib import Path

import yaml


def test_manifest_matches_spec():
    # navigate up from tests/ to the repository root
    root = Path(__file__).resolve().parent.parent.parent.parent
    spec_path = root / "spec" / "openapi" / "http.yaml"
    manifest_path = root / "spec" / "metadata" / "api_manifest.jsonld"

    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    generated_endpoints = []
    for path, methods in spec.get("paths", {}).items():
        for method, info in methods.items():
            generated_endpoints.append(
                {
                    "path": path,
                    "method": method.upper(),
                    "description": info.get("description", "").strip(),
                }
            )

    with open(manifest_path) as f:
        manifest = json.load(f)

    assert manifest["baseURL"] == spec.get("servers", [{}])[0].get("url", "")
    assert manifest.get("endpoints") == generated_endpoints
