# DeathByCaptcha Java SDK

[![Java 17](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml)
[![Java 21](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml)
[![Java 25](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml)
[![Coverage](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml)
[![Integration API](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)
[![Maven Online](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml)

Java SDK to integrate DeathByCaptcha into backend applications and Selenium-based automation.

## Documentation

Practical documentation is available in `docs/`:

- [Main guide](docs/README.md)
- [Getting started](docs/getting-started.md)
- [Maven Central online usage](docs/getting-started.md#0-use-maven-central-online)
- [Library usage](docs/library-usage.md)
- [Selenium integration](docs/selenium-integration.md)
- [CI and coverage](docs/ci-and-coverage.md)

## Project structure

- `src/main/java/com/DeathByCaptcha/`: core SDK (`Client`, `HttpClient`, `SocketClient`, models, and exceptions).
- `src/main/java/examples/`: runnable examples for multiple captcha types.
- `src/test/java/com/DeathByCaptcha/`: unit tests for the core package.
- `.gitlab-ci.yml`: GitLab pipeline with Java 17/21/25 and core coverage reporting.
- `.github/workflows/java17-tests.yml`, `.github/workflows/java21-tests.yml`, `.github/workflows/java25-tests.yml`: per-version workflows for GitHub Actions.
- `.github/workflows/coverage-tests.yml`: dedicated coverage workflow.
- `.github/workflows/integration-tests.yml`: dedicated API integration workflow.
- `.github/workflows/maven-online-tests.yml`: dedicated Maven online integration workflow.

## Essential commands

```bash
# Compile
mvn clean compile

# Run tests + JaCoCo
mvn clean test

# Run balance example
mvn exec:java -Dexec.mainClass="examples.ExampleGetBalance"

# Run Selenium reCAPTCHA v2 example
mvn exec:java -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2"
```

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
- Maven online tests run in `maven-online-tests.yml`.
- No badge metadata is committed into the repository by CI.

### GitLab CI/CD

- Java LTS matrix (17/21/25) with Maven and JaCoCo.
- Supports credentials via CI variables and local `.env` for `gitlab-ci-local`.

### Workflow status

| Java Version | Status |
|-------------|--------|
| 17 LTS | [![Java 17](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java17-tests.yml) |
| 21 LTS | [![Java 21](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java21-tests.yml) |
| 25 LTS | [![Java 25](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java25-tests.yml) |

- Integration tests: [![Integration Tests](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)
- Coverage: [![Coverage](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/coverage-tests.yml)
- API integration: [![Integration API](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)
- Maven online: [![Maven Online](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/maven-online-tests.yml)

## License

MIT. See [LICENSE](LICENSE).
