# DeathByCaptcha Agent API Metadata

> **Purpose:** provide canonical, language‑agnostic metadata for the
> DeathByCaptcha service so that AI agents and tooling can understand or
> generate clients for *any* programming language. The repository does
> **not** aim to be a Python library; any Python code is purely a
> convenience for validating that the specifications map to the
> live API.

[![Validate APIs](https://github.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/actions/workflows/validate-api.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/actions/workflows/validate-api.yml)

## Repository Layout

```
/ (root)
├─ spec/
│   ├─ openapi/
│   │   ├─ http.yaml        # OpenAPI 3 HTTP API spec
│   │   └─ sockets.yaml     # AsyncAPI/TCP socket API spec
│   └─ jsonld/
│       ├─ http_context.jsonld
│       └─ socket_context.jsonld
└─ validation/              # optional tooling to exercise the API
    └─ python/
        ├─ tests/           # pytest suite (not required for spec use)
        └─ samples/         # placeholder images/audio used by tests
```

### Spec Files

- **OpenAPI**
  - `spec/openapi/http.yaml` – describes HTTP endpoints, parameters,
    request/response schemas, examples, etc.
  - `spec/openapi/sockets.yaml` – AsyncAPI/TCP description for the
    socket‑based API.

- **JSON‑LD Contexts**
  - `spec/jsonld/http_context.jsonld` – field names, datatypes and linked
    semantics for HTTP payloads.
  - `spec/jsonld/socket_context.jsonld` – similar context for socket
    messages.

Agents and code generators should parse these YAML/JSON‑LD artifacts directly;
these are the authoritative metadata and are intentionally self‑describing.

> 💡 *The specification is kept as close as possible to the public docs at*
> `https://deathbycaptcha.com/api` *and may serve as the single source of
> truth for tooling.*

### Validation (optional)

Although the repository is spec‑centric, there is a small Python project at
`validation/python` that exercises the live API using `pytest`. Its sole
purpose is to confirm that the specs remain accurate during development.
Nothing in `validation/python` is required for the primary use case and the
code there may be removed or ignored by consumers who simply need the
specs.

To run the validation suite you need Python 3.10+ and credentials.

A quick start:

```bash
cp .env.example .env           # create credential file at repo root
# edit .env to add DBC_USERNAME / DBC_PASSWORD
pip install -r validation/python/tests/requirements.txt
pytest validation/python/tests/ -v
```

The tests make minimal assumptions about API behaviour and are deliberately
lightweight.

---

## Agent‑friendly metadata

In addition to the formal OpenAPI and AsyncAPI specifications, the
repository includes several small artifacts that make it trivial for an
AI agent to grasp the service surface:

- **JSON‑LD contexts** (`spec/jsonld/http_context.jsonld` and
  `spec/jsonld/socket_context.jsonld`) describe the meaning and data types
  of individual fields.
- A simple **endpoint manifest** at
  `spec/metadata/api_manifest.jsonld` lists the primary HTTP paths,
  methods and human-readable descriptions.  This file can be regenerated
  from the OpenAPI spec using the helper script
  `validation/python/generate_manifest.py` or validated automatically via
  the pytest test `validation/python/tests/test_manifest.py`.

Agents may load the YAML/JSON‑LD directly, but the manifest provides a
quick start by surfacing the most important endpoints without parsing the
entire OpenAPI document.

## Supported CAPTCHA Types

The DeathByCaptcha API supports solving the following CAPTCHA types:

| Type ID | CAPTCHA Type | Parameters | Use Case |
|---------|-------------|-----------|----------|
| 0 | Standard Image | `captchafile` or `captcha` (base64) | Basic image CAPTCHA |
| 2 | reCAPTCHA Coordinates | `captchafile` (screenshot) | Legacy reCAPTCHA coordinate selection |
| 3 | Image Group | `banner`, `banner_text`, `captchafile` | Select images matching a description |
| 4 | Token (v2) | `token_params` | reCAPTCHA v2 token solving |
| 5 | reCAPTCHA v3 | `token_params` + `action`, `min_score` | reCAPTCHA v3 with risk scoring |
| 8 | Geetest v3 | `geetest_params` | Geetest v3 verification |
| 9 | Geetest v4 | `geetest_params` | Geetest v4 verification |
| 11 | Text CAPTCHA | `textcaptcha` | Text-based question solving |
| 12 | Cloudflare Turnstile | `turnstile_params` | Cloudflare Turnstile token |
| 13 | Audio CAPTCHA | `audio` (base64), `language` | Audio CAPTCHA solving |
| 14 | Lemin | `lemin_params` | Lemin CAPTCHA |
| 15 | Capy | `capy_params` | Capy CAPTCHA |
| 16 | Amazon WAF | `waf_params` | Amazon WAF verification |
| 17 | Siara | `siara_params` | Siara CAPTCHA |
| 18 | Mtcaptcha | `mtcaptcha_params` | Mtcaptcha CAPTCHA |
| 19 | Cutcaptcha | `cutcaptcha_params` | Cutcaptcha CAPTCHA |
| 20 | Friendly Captcha | `friendly_params` | Friendly Captcha |
| 21 | Datadome | `datadome_params` | Datadome verification |
| 23 | Tencent | `tencent_params` | Tencent CAPTCHA |
| 24 | ATB | `atb_params` | ATB CAPTCHA |
| 25 | reCAPTCHA v2 Enterprise | `token_enterprise_params` | reCAPTCHA v2 Enterprise tokens |

## How to Use This Metadata (For AI Agents & Code Generators)

This repository provides canonical, language-agnostic specifications that AI agents can parse to understand and interact with the DeathByCaptcha API or generate client code. Here's how to use it:

### 1. Quick Start: Load the Manifest

For a quick overview of the main API endpoints, load the endpoint manifest:

```bash
curl https://raw.githubusercontent.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/main/spec/metadata/api_manifest.jsonld
```

This gives you a structured list of:
- **Primary Base URL (HTTPS)**: `https://api.dbcapi.me/api` (recommended for security)
- **Fallback Base URL (HTTP)**: `http://api.dbcapi.me/api` (legacy only)
- Available endpoints (GET/POST / for balance, POST /captcha, GET /captcha/{id}, POST /captcha/{id}/report, GET /status)
- Human-readable descriptions

### 2. Parse the OpenAPI Specification

For complete API details, parse the OpenAPI spec:

```bash
# HTTP API spec (primary endpoint)
curl https://raw.githubusercontent.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/main/spec/openapi/http.yaml

# Socket-based API spec (alternative transport)
curl https://raw.githubusercontent.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/main/spec/openapi/sockets.yaml
```

The OpenAPI spec includes:
- All request/response schemas
- Parameter documentation
- Supported CAPTCHA types (0-25) with required fields
- Authentication methods (username/password or authtoken)
- Error codes and meanings
- Example payloads for each type

### 3. Understand Data Types with JSON-LD Contexts

JSON-LD contexts provide semantic meaning for API fields:

```bash
# HTTP context (field definitions, data types, linked semantics)
curl https://raw.githubusercontent.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/main/spec/jsonld/http_context.jsonld

# Socket context (for message-based transport)
curl https://raw.githubusercontent.com/deathbycaptcha/deathbycaptcha-agent-api-metadata/main/spec/jsonld/socket_context.jsonld
```

### 4. Typical AI Integration Flow

```
1. Agent queries metadata endpoint manifest
   ↓
2. Agent determines which CAPTCHA type matches the use case
   ↓
3. Agent parses OpenAPI spec for that type's required parameters
   ↓
4. Agent constructs request with proper authentication (authtoken or username/password)
   ↓
5. Agent sends request to POST /captcha with type-specific parameters
   ↓
6. Agent receives captcha ID from response
   ↓
7. Agent polls GET /captcha/{id} until status changes (is_correct=true or error)
   ↓
8. Agent extracts solution from response text field
   ↓
9. If incorrect, agent can POST /captcha/{id}/report to flag for review
```

### 5. Code Generation

Given the OpenAPI spec, standard tools can auto-generate clients:

```bash
# Using OpenAPI Generator (Java, Python, TypeScript, Go, etc.)
openapi-generator-cli generate \
  -i spec/openapi/http.yaml \
  -g python \
  -o generated_client_python
```

### 6. Key Fields for Agent Implementation

#### Request Authentication (for authenticated actions):
- **username + password**: form fields in request body/query (official docs)
- **authtoken**: form field in request body/query (official docs)

#### Check Balance (`GET /` or `POST /`)
- Returns `user`, `rate`, `balance`, `is_banned`, `status`

#### Upload a CAPTCHA (`POST /captcha`)
Required fields depend on type:
- `username` or `authtoken` (authentication)
- `type` (integer 0-25, selects CAPTCHA type)
- Type-specific parameters (e.g., `token_params` for type 4, `audio` + `language` for type 13)

#### Poll for Solution (`GET /captcha/{captchaId}`)
- Returns `CaptchaResponse` with:
  - `captcha`: captcha ID
  - `text`: solution (null if not yet solved)
  - `is_correct`: boolean (true when solved)
  - `status`: HTTP status (0 for success)

#### Check Server Status (`GET /status`)
- `todays_accuracy`: daily success rate
- `solved_in`: average solve time (seconds)
- `is_service_overloaded`: queue status

---

## Practical Example: AI Agent Generating Client Code

### Scenario
An AI agent is asked: *"Generate TypeScript code to check my DeathByCaptcha account balance and API service status using the official metadata specs."*

The agent:
1. Parses `spec/openapi/http.yaml` to understand endpoints
2. Identifies `/` (balance) and `/status` endpoints
3. Generates type-safe client code

### Generated Code (TypeScript)

```typescript
import axios from 'axios';

interface UserResponse {
  user: number;
  rate: number;
  balance: number;
  is_banned: boolean;
  status: number;
}

interface ServerStatus {
  status: number;
  todays_accuracy: number;
  solved_in: number;
  is_service_overloaded: boolean;
}

class DeathByCaptchaClient {
  private baseURL = 'https://api.dbcapi.me/api';  // Use HTTPS for security
  private authToken: string;

  constructor(authToken: string) {
    this.authToken = authToken;
  }

  async checkBalance(): Promise<UserResponse> {
    try {
      const form = new URLSearchParams({ authtoken: this.authToken });
      const response = await axios.post<UserResponse>(
        `${this.baseURL}/`,
        form,
        {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Accept': 'application/json'
          }
        }
      );
      return response.data;
    } catch (error) {
      throw new Error(`Failed to check balance: ${error.message}`);
    }
  }

  async getServiceStatus(): Promise<ServerStatus> {
    try {
      const response = await axios.get<ServerStatus>(
        `${this.baseURL}/status`
      );
      return response.data;
    } catch (error) {
      throw new Error(`Failed to get service status: ${error.message}`);
    }
  }

  async reportStatus(): Promise<void> {
    try {
      const balance = await this.checkBalance();
      const status = await this.getServiceStatus();

      console.log('=== DeathByCaptcha Account Status ===');
      console.log(`User ID: ${balance.user}`);
      console.log(`Balance (credits): ${balance.balance}`);
      console.log(`Rate (per solved captcha): ${balance.rate}`);
      console.log(`Account Banned: ${balance.is_banned}`);
      console.log('\n=== Service Status ===');
      console.log(`Accuracy Today: ${status.todays_accuracy}%`);
      console.log(`Avg Solve Time: ${status.solved_in}s`);
      console.log(`Service Overloaded: ${status.is_service_overloaded}`);
    } catch (error) {
      console.error('Error:', error.message);
    }
  }
}

// Usage
const client = new DeathByCaptchaClient('your_auth_token_here');
await client.reportStatus();
```

### Usage & Output

```bash
$ npx ts-node client.ts

=== DeathByCaptcha Account Status ===
User ID: 12345
Balance: $250.50
Hourly Rate: $10.00
Account Banned: false

=== Service Status ===
Accuracy Today: 99.6%
Avg Solve Time: 8.5s
Service Overloaded: false
```

### Another Example: Solving a CAPTCHA (Python)

An agent asked to: *"Generate Python code to solve a reCAPTCHA v2 token challenge"*

```python
import requests
import time
import json
from typing import Optional

class DBCCaptchaSolver:
    def __init__(self, username: str, password: str):
        self.base_url = "https://api.dbcapi.me/api"  # Use HTTPS for security
        self.username = username
        self.password = password

    def solve_recaptcha_v2(
        self,
        googlekey: str,
        pageurl: str,
        proxy: Optional[str] = None,
        proxytype: str = "HTTP"
    ) -> str:
        """
        Solve reCAPTCHA v2 token (type 4)
        """
        token_params = {
            "googlekey": googlekey,
            "pageurl": pageurl,
        }
        if proxy:
            token_params["proxy"] = proxy
            token_params["proxytype"] = proxytype

        # Upload captcha
        payload = {
            "username": self.username,
            "password": self.password,
            "type": 4,  # reCAPTCHA v2 token
            "token_params": json.dumps(token_params)
        }

        response = requests.post(f"{self.base_url}/captcha", data=payload)
        response.raise_for_status()
        result = response.json()

        if result.get("status") != 0:
            raise Exception(f"Upload failed: {result}")

        captcha_id = result["captcha"]
        print(f"Captcha uploaded, ID: {captcha_id}")

        # Poll for solution
        max_attempts = 60
        for attempt in range(max_attempts):
            time.sleep(2)  # Wait before polling
            poll_response = requests.get(
                f"{self.base_url}/captcha/{captcha_id}"
            )
            poll_response.raise_for_status()
            poll_data = poll_response.json()

            if poll_data.get("is_correct"):
                token = poll_data.get("text")
                print(f"Captcha solved! Token: {token}")
                return token

            print(f"Attempt {attempt + 1}: Still solving...")

        raise TimeoutError("Captcha solving timeout after 60 polls")

# Usage
solver = DBCCaptchaSolver(
    username="your_username",
    password="your_password"
)

token = solver.solve_recaptcha_v2(
    googlekey="6Le-wvkSAAAAAPBMRTvw0Q4Muexq9bi0DJwx_mJ-",
    pageurl="http://test.com/path_with_recaptcha",
    proxy="http://127.0.0.1:3128",
    proxytype="HTTP"
)

print(f"Final token: {token}")
```

### Output

```
Captcha uploaded, ID: 98765
Attempt 1: Still solving...
Attempt 2: Still solving...
Attempt 3: Captcha solved! Token: 03AGdBq27qwuIu4...
Final token: 03AGdBq27qwuIu4...
```

---

## Contribution & CI

- Update the spec files whenever the DeathByCaptcha API changes.
- Optionally run the Python validation suite locally to catch drift.
- GitHub Actions and GitLab CI pipelines (see `.github/workflows/validate-api.yml` and
  `.gitlab-ci.yml`) use the validation tests; they trigger on changes to
  the `spec/` or `validation/python/tests/` directories.

