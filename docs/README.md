# Java Client Library Documentation

This is the canonical documentation index for the DeathByCaptcha Java client library.

Use this page as the entry point for setup, integration, and CI guidance.

## Main guides

- [Getting started](getting-started.md): setup, compilation, and first credentials check (includes Maven Central vs. Git source).
- [Samples usage](samples.md): where samples live and how to run them with Maven profile `samples`.
- [Library usage](library-usage.md): authentication, `HttpClient`/`SocketClient`, upload and solve flows.
- [Selenium integration](selenium-integration.md): end-to-end reCAPTCHA v2 workflow in automated browsers.
- [CI and coverage](ci-and-coverage.md): Java LTS (17/21/25), JaCoCo reports, and pipeline setup.

## Recommended path

1. Start with [Getting started](getting-started.md) to choose your installation method (Maven Central vs. Git source).
2. If you want runnable examples, follow [Samples usage](samples.md).
3. If you are integrating into backend services or scripts, continue with [Library usage](library-usage.md).
4. If you automate browsers, follow [Selenium integration](selenium-integration.md).
5. For team validation and quality gates, implement [CI and coverage](ci-and-coverage.md).

## Requirements

- **Java**: 25 LTS (recommended) or 17/21 (supported).
- **Maven**: 3.9+.
- **Credentials**: Active DeathByCaptcha account with valid API credentials.

## Best practices

- Do not hardcode credentials in source files.
- Use environment variables or CI secrets.
- Report incorrect solutions with `client.report(captchaId)` to improve solution quality.
- Prefer `HttpClient` to start, and use `SocketClient` for higher throughput with TCP ports 8123-8130 open.
