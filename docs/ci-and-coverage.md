# CI and coverage

This guide explains how to run SDK tests and coverage on Java LTS 17, 21, and 25.

## Goal

- Run unit tests for the core SDK.
- Generate JaCoCo coverage.
- Show CI coverage specifically for package `com.DeathByCaptcha`.

## Local execution

```bash
mvn -Djava.release=25 -Dtest='!Online*IntegrationTest' clean test
```

For other LTS versions:

```bash
mvn -Djava.release=17 -Dtest='!Online*IntegrationTest' clean test
mvn -Djava.release=21 -Dtest='!Online*IntegrationTest' clean test
```

To run online integration tests only:

```bash
mvn -Djava.release=25 -Dtest=Online*IntegrationTest test
```

## Generated reports

- Tests: `target/surefire-reports/`
- HTML coverage: `target/site/jacoco/index.html`
- CSV coverage: `target/site/jacoco/jacoco.csv`

## Calculate core coverage via CLI

```bash
awk -F',' 'NR>1 && $2 == "com.DeathByCaptcha" { missed += $4; covered += $5 } END { total = missed + covered; pct = (total > 0 ? (covered * 100.0 / total) : 0); printf "Coverage (core): %.2f%%\n", pct }' target/site/jacoco/jacoco.csv
```

## GitLab CI

File: `.gitlab-ci.yml`

- Java matrix: 17, 21, 25.
- JDK 25 image: `maven:3-eclipse-temurin-25`.
- JaCoCo: version `0.8.13` (Java 25 compatible).
- Coverage regex: extracts `Coverage (core): <value>%`.

## GitHub Actions

Files:

- `.github/workflows/java17-tests.yml`
- `.github/workflows/java21-tests.yml`
- `.github/workflows/java25-tests.yml`
- `.github/workflows/coverage-tests.yml`
- `.github/workflows/integration-tests.yml`
- `.github/workflows/maven-online-tests.yml`
- `.github/workflows/publish-maven-central.yml`

- Core tests run per Java version and exclude `Online*IntegrationTest`.
- Coverage runs separately in `coverage-tests.yml` (Java 25, excluding online integration tests).
- API integration runs in `integration-tests.yml` (Java 25, `OnlineGitBasicApiIntegrationTest`).
- Maven online integration runs in `maven-online-tests.yml` (Java 25 runner, `OnlineMavenBalanceIntegrationTest` validating Maven Central consumption).
- Publishes test and coverage reports as artifacts.
- Publishes `coverage-badge.json` to GitHub Pages from Actions (no commit-bot updates to the repository).
- Publishes to Maven Central on GitHub Release publish via `publish-maven-central.yml` with `-Djava.release=17` (compatible with Java 17/21/25 runtimes).
- Maven Central publish is restricted to stable releases (pre-releases/drafts are skipped).

Required secrets for Maven Central publish:

- `CENTRAL_USERNAME`
- `CENTRAL_PASSWORD`
- `MAVEN_GPG_PRIVATE_KEY` (ASCII-armored private key)
- `MAVEN_GPG_PASSPHRASE` (optional; only needed if the private key is passphrase-protected)

## Recommendations

- Keep JaCoCo updated for new Java versions.
- Measure coverage by target package when the repository includes examples.
- Avoid using global coverage if your quality gate applies only to the core SDK.
