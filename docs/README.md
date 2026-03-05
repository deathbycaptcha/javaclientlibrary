# Java SDK Documentation

This folder contains practical guides for using the DeathByCaptcha Java SDK across common implementation scenarios.

## Main guides

- [Getting started](getting-started.md): setup, compilation, and first credentials check.
- [Library usage](library-usage.md): authentication, `HttpClient`/`SocketClient`, upload and solve flows.
- [Selenium integration](selenium-integration.md): end-to-end reCAPTCHA v2 workflow in automated browsers.
- [CI and coverage](ci-and-coverage.md): Java LTS (17/21/25), JaCoCo reports, and pipeline setup.

## Recommended path

1. Start with [Getting started](getting-started.md).
2. If you are integrating into backend services or scripts, continue with [Library usage](library-usage.md).
3. If you automate browsers, follow [Selenium integration](selenium-integration.md).
4. For team validation and quality gates, implement [CI and coverage](ci-and-coverage.md).

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
