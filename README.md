# DeathByCaptcha Java SDK

Java SDK to integrate DeathByCaptcha into backend applications and Selenium-based automation.

## Documentation

Practical documentation is available in `docs/`:

- [Main guide](docs/README.md)
- [Getting started](docs/getting-started.md)
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

## License

MIT. See [LICENSE](LICENSE).
