# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.2] - 2026-03-02

### Added

- **Agent‑friendly metadata**
  - `spec/metadata/api_manifest.jsonld` provides a concise list of HTTP
    endpoints and descriptions.
  - `validation/python/generate_manifest.py` script to regenerate the manifest
    from the OpenAPI spec.
  - `validation/python/tests/test_manifest.py` ensures manifest stays in sync.
  - README expanded with instructions and references to the manifest and
    JSON‑LD contexts.

### Removed

- Obsolete Markdown reports (`API_VALIDATION_REPORT.md`,
  `ADDITIONAL_VALIDATION_REPORT.md`, `SPEC_REVIEW.md`) cleaned from root.

### Changed

- Minor spec cleanup for YAML parsing (indentation fixes).
- Documentation updates across README and SPEC_REVIEW (now archived).

## [1.0.1] - 2026-03-02

### Changed

- Restructured repository to emphasize API metadata over Python validation.  
  - Specs moved under `spec/` with subdirectories `openapi/` and `jsonld/`.  
  - Validation code relocated to `validation/python/`; original tests no longer at top level.  
  - README rewritten to focus on spec usage, with validation instructions marked optional.  
  - CI workflows and documentation updated to reference new paths.  
  - Changelog and other documentation updated accordingly.

### Removed

- Python examples and validation utilities no longer pollute root; kept only under `validation/`.  
- Sample files moved into validation directory.  
- Top-level `tests/` directory removed.


## [1.0.0] - 2026-02-26

### Added

- **Pytest Test Suite**: Complete refactor of validation scripts as pytest tests
  - `tests/test_http_api.py` – HTTP API tests (auth, balance, uploads)
  - `tests/test_socket_api.py` – Socket API tests (login, uploads, token APIs)
  - `tests/conftest.py` – Shared fixtures for credentials and sample files
  - `tests/requirements.txt` – Test dependencies (pytest, requests)

- **CI/CD Pipelines**:
  - `.github/workflows/validate-api.yml` – GitHub Actions workflow
    - Daily schedule at 00:00 UTC
    - Runs on push/PR to OpenAPI specs, JSON-LD contexts, tests
    - Credentials loaded from GitHub Secrets (`DBC_USERNAME`, `DBC_PASSWORD`)
  - `.gitlab-ci.yml` – GitLab CI/CD configuration
    - Runs on push to main/develop and merge requests
    - Credentials loaded from GitLab CI/CD Variables
    - Optional nightly schedule support
    - Automatic retries on system failure
    - Test artifacts (JUnit XML reports)

- **Credential Management**:
  - `.env.example` – Template for local environment configuration
  - Local `.env` file loading in validators (ignored by git)
  - Environment variable support (`DBC_USERNAME`, `DBC_PASSWORD`)
  - Secure credential handling via CI/CD platform secrets

- **Documentation**:
  - Enhanced README.md with:
    - AI-agent optimization notes
    - Reference to https://deathbycaptcha.com/api
    - Setup instructions for local testing
    - GitHub Actions and GitLab CI/CD setup guides
    - Test coverage details

- **Repository Structure**:
  - Updated .gitignore to exclude `.env` and CI cache files
  - Created .github/workflows directory for actions
  - Organized tests in dedicated `tests/` directory
  - Added repository metadata and reference links

### Changed

- **Validation Approach**: Migrated from standalone scripts to pytest framework
  - More granular test organization
  - Better error reporting and assertion messages
  - Fixture-based resource management (credentials, sample files)
  - Cleaner output and CI integration

- **Credential Handling**: Transitioned from hardcoded values to secure sources
  - Environment variables (priority for CI/CD)
  - Local `.env` file (development)
  - Explicit fallback mechanism

- **README.md Content**:
  - Refocused on AI-agent metadata consumption
  - Added explicit CI/CD platform instructions
  - Consolidated test coverage documentation
  - Clarified path and file structure for automation

### Removed

- Hardcoded test credentials from source code
- Loose validation scripts (replaced by pytest tests)
- Generic CI/CD notes (replaced with platform-specific guides)

### Security

- `.env` file added to `.gitignore` (prevents accidental credential commits)
- GitHub Secrets and GitLab CI/CD Variables for safe credential injection
- Test fixtures validate credential availability before running tests

### Testing

- HTTP API: User auth, balance checks, image/coordinates/group/token uploads
- Socket API: Login, user info, same upload types via TCP
- All tests assert non-501 responses for core captcha types
- 501/placeholder responses expected for types requiring third-party credentials

### Documentation

- Complete setup instructions for local and CI environments
- CI/CD platform-specific configuration guides
- Architecture notes for external contributors and AI agents

---

## Repository Structure

```
/ (root)
├─ spec/
│   ├─ openapi/
│   │   ├─ http.yaml        # OpenAPI 3 HTTP API spec
│   │   └─ sockets.yaml     # AsyncAPI/TCP socket API spec
│   └─ jsonld/
│       ├─ http_context.jsonld
│       └─ socket_context.jsonld
├─ validation/
│   └─ python/
│       ├─ tests/           # pytest suite (optional validation)
│       └─ samples/         # placeholder images/audio used by tests
├─ .env.example              # Credential template
├─ .gitignore               # Git ignore patterns
├─ README.md                # Main documentation
├─ CHANGELOG.md             # This file
└─ LICENSE                  # License file
```

---

## How to Use

### Local Testing

```bash
cp .env.example .env
# Edit .env with your DBC test credentials
pip install -r tests/requirements.txt
pytest tests/ -v
```

### GitHub Actions

1. Go to **Settings → Secrets and variables → Actions**
2. Add `DBC_USERNAME` and `DBC_PASSWORD`
3. Tests run on push and daily schedule

### GitLab CI/CD

1. Go to **Settings → CI/CD → Variables**
2. Add `DBC_USERNAME` and `DBC_PASSWORD`
3. Tests run on push and merge requests; create schedule for nightly runs

---

## References

- API Docs: https://deathbycaptcha.com/api
- OpenAPI Specs: openapi_http.yaml, openapi_sockets.yaml
- JSON-LD Contexts: jsonld/
