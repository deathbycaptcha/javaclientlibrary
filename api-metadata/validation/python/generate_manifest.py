#!/usr/bin/env python3
"""Generate a simple JSON-LD manifest from the OpenAPI HTTP spec.

The manifest is stored at spec/metadata/api_manifest.jsonld and mirrors the
structure used by the repository.  Regenerating it ensures the manifest stays
in sync with the official spec.

Usage:
    python validation/python/generate_manifest.py

"""
import json
from pathlib import Path

import yaml


def main():
    root = Path(__file__).parent.parent.parent
    spec_path = root / "spec" / "openapi" / "http.yaml"
    out_path = root / "spec" / "metadata" / "api_manifest.jsonld"

    with open(spec_path) as f:
        spec = yaml.safe_load(f)

    endpoints = []
    for path, methods in spec.get("paths", {}).items():
        for method, info in methods.items():
            endpoints.append({
                "path": path,
                "method": method.upper(),
                "description": info.get("description", "").strip()
            })

    manifest = {
        "@context": {
            "dbc": "https://deathbycaptcha.com/schema#",
            "baseURL": "dbc:baseURL",
            "endpoint": "dbc:endpoint",
            "path": "dbc:path",
            "method": "dbc:method",
            "description": "dbc:description"
        },
        "baseURL": spec.get("servers", [{}])[0].get("url", ""),
        "endpoints": endpoints
    }

    out_path.parent.mkdir(parents=True, exist_ok=True)
    with open(out_path, "w") as f:
        json.dump(manifest, f, indent=2)

    print(f"Generated manifest to {out_path}")


if __name__ == "__main__":
    main()
