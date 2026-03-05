# DeathByCaptcha Java SDK

[![Tests](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml/badge.svg)](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml)
[![Coverage](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/deathbycaptcha/javaclientlibrary/master/.coverage/badge.json)](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml)

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
- Coverage badge generated as JSON metadata at `.coverage/badge.json`.
- Per-version badge metadata generated at `.badges/java17|java21|java25/badge.json`.

### GitLab CI/CD

- Java LTS matrix (17/21/25) with Maven and JaCoCo.
- Supports credentials via CI variables and local `.env` for `gitlab-ci-local`.

### Test status by Java version

| Java Version | Status |
|-------------|--------|
| 17 LTS | [![Java 17](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/deathbycaptcha/javaclientlibrary/master/.badges/java17/badge.json)](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml) |
| 21 LTS | [![Java 21](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/deathbycaptcha/javaclientlibrary/master/.badges/java21/badge.json)](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml) |
| 25 LTS | [![Java 25](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/deathbycaptcha/javaclientlibrary/master/.badges/java25/badge.json)](https://github.com/deathbycaptcha/javaclientlibrary/actions/workflows/java-lts-tests.yml) |

## License

MIT. See [LICENSE](LICENSE).
