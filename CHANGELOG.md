# Changelog

All notable changes to this project are documented in this file.

## [4.6.9] - 2026-03-06

### Changed

- Bumped library/version metadata from `4.6.8` to `4.6.9` in:
  - `pom.xml`
  - `src/main/java/com/DeathByCaptcha/Client.java` (`API_VERSION`)
  - `src/test/java/com/DeathByCaptcha/ClientTest.java`
- Updated Maven Central publish workflow to deploy artifacts with `-Djava.release=17` for Java 17/21/25 runtime compatibility.
- Updated Maven Central online integration messaging/docs to remove Java 25-only requirement.

## [4.6.8] - 2026-03-06

### Changed

- Bumped library/version metadata from `4.6.7` to `4.6.8` in:
  - `pom.xml`
  - `src/main/java/com/DeathByCaptcha/Client.java` (`API_VERSION`)
  - `src/test/java/com/DeathByCaptcha/ClientTest.java`
- Updated Selenium sample and Selenium integration test to use Chrome by default in headless CI-friendly mode.
- Added stricter Selenium integration assertion to require `Verification Success` after form submission.
- Updated Selenium integration workflow diagnostics and browser setup flow to avoid unstable GeckoDriver action dependency.
- Made all runnable classes in `src/main/java/examples/` public so `mvn exec:java -Dexec.mainClass=...` works consistently across samples.

## [4.6.7] - 2026-03-06

### Changed

- Bumped library/version metadata from `4.6.6` to `4.6.7` in:
  - `pom.xml`
  - `src/main/java/com/DeathByCaptcha/Client.java` (`API_VERSION`)
  - `src/test/java/com/DeathByCaptcha/ClientTest.java`
  - `src/test/java/com/DeathByCaptcha/OnlineMavenBalanceIntegrationTest.java`
  - `docs/getting-started.md`
  - `docs/library-usage.md`
- Added release automation workflow: `.github/workflows/publish-maven-central.yml`.
- Restricted Maven Central auto-publish to stable releases only (pre-releases and drafts are skipped).

## [4.6.6] - 2026-03-05

### Added

- Online Maven integration test in `src/test/java/com/DeathByCaptcha/OnlineMavenBalanceIntegrationTest.java` to validate:
  - Resolution of `io.github.deathbycaptcha:deathbycaptcha-java-library:4.6.6` from Maven Central.
  - Balance check flow using a temporary Maven project.
  - Isolated local Maven repository during test execution.
- Online API integration test in `src/test/java/com/DeathByCaptcha/OnlineGitBasicApiIntegrationTest.java` (uses current git source) to validate:
  - `connect()` and user retrieval (`getUser()`).
  - `getBalance()` with non-negative assertion.
  - Text captcha upload (`type=0`), polling by `getCaptcha()`, and solved text retrieval.
- Local helper script `run-tests.sh` to load credentials from `.env` safely and run Maven commands.
- Root `CHANGELOG.md`.

### Changed

- `.gitlab-ci.yml`
  - Added `.env` loading support for `DBC_USERNAME` and `DBC_PASSWORD` in `before_script`.
  - Kept Maven execution aligned with CI matrix across Java 17/21/25.
- `.github/workflows/java-lts-tests.yml`
  - Expanded from matrix-only run to explicit jobs:
    - `test-java17`, `test-java21`, `test-java25`
    - `coverage` job that computes coverage percentage and writes `.coverage/badge.json`
    - `badges` job that generates `.badges/java17|java21|java25/badge.json`
  - Added automatic badge commits on `main`/`master` pushes.
- `README.md`
  - Added GitHub Actions and coverage badges (same badge strategy used in the PHP client project).
  - Added CI status section with per-Java-version badges.
  - Added direct link to Maven Central online usage docs.
- `.env.example`
  - Simplified to minimum required credentials (`DBC_USERNAME`, `DBC_PASSWORD`).

### Documentation

- `docs/getting-started.md`
  - Added Maven Central online dependency usage (`io.github.deathbycaptcha:deathbycaptcha-java-library:4.6.6`).
  - Added online artifact resolution command via `maven-dependency-plugin:get`.
  - Added integration test execution examples (`OnlineMavenBalanceIntegrationTest`, `OnlineGitBasicApiIntegrationTest`).
- `docs/library-usage.md`
  - Added Maven Central installation section with dependency snippet and online resolution check.
- `docs/README.md`
  - Added explicit Maven Central online usage entry and updated recommended docs path.

### Notes

- Maven Central (`repo1.maven.org`) already serves version `4.6.6`.
- Third-party indexers (e.g. MvnRepository) may take additional time to reflect new versions.
