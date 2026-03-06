# DeathByCaptcha Java Client Library

[![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fdeathbycaptcha.github.io%2Fdeathbycaptcha-api-client-java%2Fcoverage-badge.json&cacheSeconds=300)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml)
[![Integration API](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)
[![Selenium Integration](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/selenium-integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/selenium-integration-tests.yml)

Java client library to integrate DeathByCaptcha into backend applications and Selenium-based automation.

## Documentation

Documentation lives in `docs/`.

- [Documentation hub](docs/README.md)
- [Quick start from Git source](docs/getting-started.md#option-b-use-this-git-repository)
- [Use from Maven Central](docs/getting-started.md#option-a-use-maven-central-online)
- [Selenium Sample](docs/selenium-integration.md)

## Project structure

- `src/main/java/com/DeathByCaptcha/`
  - Core client library (`Client`, `HttpClient`, `SocketClient`, models, and exceptions).
- `src/main/java/examples/`
  - Runnable examples for reCAPTCHA v2/v3, image coordinates, image groups, and Selenium integration.
- `src/test/java/com/DeathByCaptcha/`
  - Unit tests for core client functionality.
  - Integration tests for live API validation.
  - Maven Central artifact availability tests.
- `.github/workflows/`
  - `java17-tests.yml`, `java21-tests.yml`, `java25-tests.yml`: Per-version Java testing.
  - `coverage-tests.yml`: Code coverage reporting with JaCoCo.
  - `integration-tests.yml`: Live API integration tests.
  - `selenium-integration-tests.yml`: Headless Selenium reCAPTCHA integration tests.
  - `maven-online-tests.yml`: Maven Central availability tests.
  - `publish-maven-central.yml`: Automated publishing on release.
- `docs/`
  - `getting-started.md`: Installation and setup guide.
  - `library-usage.md`: API usage and supported CAPTCHA types.
  - `selenium-integration.md`: Selenium automation guide.
  - `ci-and-coverage.md`: CI/CD configuration details.


## Requirements

- Java 25 LTS (recommended)
- Java 17/21 (supported)
- Maven 3.9+
- Valid DeathByCaptcha credentials

## Continuous Integration

This project is configured for automated testing on multiple platforms:

### GitHub Actions

- Tested Java versions: 17 (LTS), 21 (LTS), 25 (latest LTS) in independent workflows.
- Coverage runs in a dedicated workflow: `coverage-tests.yml`.
- API integration tests run in `integration-tests.yml`.
- Selenium integration tests run in `selenium-integration-tests.yml`.
- Maven online tests run in `maven-online-tests.yml`.
- Maven Central publish runs on Release publish: `publish-maven-central.yml`.
- No badge metadata is committed into the repository by CI.

### GitLab CI/CD

- Java LTS matrix (17/21/25) with Maven and JaCoCo.
- Supports credentials via CI variables and local `.env` for `gitlab-ci-local`.

### Workflow status

| Type | Workflow | Status |
|------|----------|--------|
| Java LTS | Java 17 | [![Java 17](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml) |
| Java LTS | Java 21 | [![Java 21](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml) |
| Java LTS | Java 25 | [![Java 25](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml) |
| Quality | Coverage | [![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fdeathbycaptcha.github.io%2Fdeathbycaptcha-api-client-java%2Fcoverage-badge.json&cacheSeconds=300)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml) |
| Integration | API integration | [![Integration API](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml) |
| Integration | Selenium integration | [![Selenium Integration](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/selenium-integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/selenium-integration-tests.yml) |
| Integration | Maven online | [![Maven Online](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml) |

## License

MIT. See [LICENSE](LICENSE).
