# CI and coverage

This guide explains how to run SDK tests and coverage on Java LTS 17, 21, and 25.

## Goal

- Run unit tests for the core SDK.
- Generate JaCoCo coverage.
- Show CI coverage specifically for package `com.DeathByCaptcha`.

## Local execution

```bash
mvn -Djava.release=25 clean test
```

For other LTS versions:

```bash
mvn -Djava.release=17 clean test
mvn -Djava.release=21 clean test
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

File: `.github/workflows/java-lts-tests.yml`

- Java matrix: 17, 21, 25.
- Runs `mvn clean test` with `-Djava.release`.
- Publishes test and coverage reports as artifacts.

## Recommendations

- Keep JaCoCo updated for new Java versions.
- Measure coverage by target package when the repository includes examples.
- Avoid using global coverage if your quality gate applies only to the core SDK.
