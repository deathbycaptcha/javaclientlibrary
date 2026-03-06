# Java Client Library Documentation

This is the canonical documentation index for the DeathByCaptcha Java client library.

Use this page as the entry point for setup, integration, and CI guidance.

## Main guides

- [Getting started](getting-started.md): setup, compilation, and first credentials check.
- [Maven Central online usage](getting-started.md#option-a-use-maven-central-online): dependency setup and resolution checks from Central.
- [Library usage](library-usage.md): authentication, `HttpClient`/`SocketClient`, upload and solve flows.
- [Selenium integration](selenium-integration.md): end-to-end reCAPTCHA v2 workflow in automated browsers.
- [CI and coverage](ci-and-coverage.md): Java LTS (17/21/25), JaCoCo reports, and pipeline setup.

## Recommended path

1. Start with [Getting started](getting-started.md).
2. If you consume the SDK as a dependency, follow [Maven Central online usage](getting-started.md#option-a-use-maven-central-online).
3. If you are integrating into backend services or scripts, continue with [Library usage](library-usage.md).
4. If you automate browsers, follow [Selenium integration](selenium-integration.md).
5. For team validation and quality gates, implement [CI and coverage](ci-and-coverage.md).

## General requirements

- Java 25 LTS (recommended).
- Java 17/21 are supported for compatibility.
- Maven 3.9+.
- Active DeathByCaptcha account with valid credentials.

## Best practices

- Do not hardcode credentials in source files.
- Use environment variables or CI secrets.
- Report incorrect solutions with `client.report(captchaId)` to improve solution quality.
- Prefer `HttpClient` to start, and use `SocketClient` for higher throughput with TCP ports 8123-8130 open.
