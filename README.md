# DeathByCaptcha Java SDK

[![Tests](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java-lts-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java-lts-tests.yml)
[![Integration Tests](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml/badge.svg)](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)

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
- `.github/workflows/java-lts-tests.yml`: equivalent workflow for GitHub Actions.

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

- Tested Java versions: 17 (LTS), 21 (LTS), 25 (latest LTS).
- Integration tests run in a dedicated workflow: `integration-tests.yml`.
- No badge metadata is committed into the repository by CI.

### GitLab CI/CD

- Java LTS matrix (17/21/25) with Maven and JaCoCo.
- Supports credentials via CI variables and local `.env` for `gitlab-ci-local`.

### Workflow status

- Core tests: [Java LTS Tests workflow](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/java-lts-tests.yml)
- Integration tests: [Integration Tests workflow](https://github.com/deathbycaptcha/deathbycaptcha-api-client-java/actions/workflows/integration-tests.yml)

## License

MIT. See [LICENSE](LICENSE).
