# Getting started

Guide to validate your environment compiles correctly and the SDK can authenticate successfully.

## Choose your path

- **Option A (recommended for consumers):** use Maven Central.
- **Option B (recommended for contributors):** use this Git repository source code.

## Option A: Use Maven Central (online)

If you want to consume the SDK from Maven Central (instead of source code in this repository), use:

- GroupId: `io.github.deathbycaptcha`
- ArtifactId: `deathbycaptcha-java-library`
- Version: `LATEST`

`pom.xml` dependency:

```xml
<dependency>
  <groupId>io.github.deathbycaptcha</groupId>
  <artifactId>deathbycaptcha-java-library</artifactId>
  <version>LATEST</version>
</dependency>
```

Quick online resolution check:

```bash
mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.7.1:get \
  -Dartifact=io.github.deathbycaptcha:deathbycaptcha-java-library:LATEST \
  -Dtransitive=false \
  -DremoteRepositories=central::default::https://repo1.maven.org/maven2
```

Expected result: `BUILD SUCCESS`.

## Option B: Use this Git repository

### 1) Clone and enter the repo

```bash
git clone https://github.com/deathbycaptcha/deathbycaptcha-api-client-java.git
cd deathbycaptcha-api-client-java
```

### 2) Prepare your environment

- Java 25 LTS (recommended).
- Java 17 or 21 (supported for compatibility).
- Maven 3.9+.
- DeathByCaptcha credentials (for running examples and tests).

**For running examples**: Pass credentials as command-line arguments (see section 4) or edit the source code (see section 6).

**For running integration tests** (optional): Set environment variables or create a `.env` file:

```bash
export DBC_USERNAME="your_username"
export DBC_PASSWORD="your_password"
# or
export DBC_AUTHTOKEN="your_token"
```

The `run-tests.sh` helper script automatically loads variables from a `.env` file if present.

### 3) Compile the project

From the repository root:

```bash
mvn clean compile
```

### 4) Run the balance example

The fastest way to validate connectivity and authentication.

`ExampleGetBalance` requires 3 command-line parameters:
1. Username
2. Password
3. API type: `HTTP` or `SOCKET`

Pass parameters using `-Dexec.args`:

```bash
mvn exec:java -Dexec.mainClass="examples.ExampleGetBalance" \
  -Dexec.args="your_username your_password HTTP"
```

Or using Socket API:

```bash
mvn exec:java -Dexec.mainClass="examples.ExampleGetBalance" \
  -Dexec.args="your_username your_password SOCKET"
```

With valid credentials, you should see output similar to:

```text
Using HTTP API
Your balance is 1234 US cents
```

### 5) Run tests and coverage

Run unit tests (excludes Selenium integration tests that require Chrome):

```bash
mvn clean test -Dtest='!OnlineSeleniumRecaptchaIntegrationTest'
```

Or use the helper script (automatically loads variables from `.env` file):

```bash
./run-tests.sh
```

To run all tests including Selenium (requires Chrome/ChromeDriver installed):

```bash
mvn clean test
# or
./run-tests.sh test
```

Generated artifacts:

- Test report: `target/surefire-reports/`
- JaCoCo coverage: `target/site/jacoco/`

**Note:** The `OnlineSeleniumRecaptchaIntegrationTest` requires Chrome browser and ChromeDriver to be installed. If you don't have them, use the first command to skip that test.

### 6) Run a real captcha example

Most examples (unlike `ExampleGetBalance`) require editing the source code to set credentials and captcha parameters before running.

For example, edit [ExampleRecaptchaV2.java](../src/main/java/examples/ExampleRecaptchaV2.java):

```java
String username = "your_username_here";  // Update this
String password = "your_password_here";  // Update this
String googlekey = "6LfW6wATAAAAAHLqO2pb8bDBahxlMxNdo9g947u9";  // Use real site key
String pageurl = "https://recaptcha-demo.appspot.com/recaptcha-v2-checkbox.php";  // Use real page URL
```

After updating the code, recompile and run:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="examples.ExampleRecaptchaV2"
```

The same applies to other captcha examples in the `examples/` package.

### 7) Validate online integration tests

These tests hit the real API and require valid credentials.

**Note:** The `OnlineMavenBalanceIntegrationTest` runs on Java 17+ and validates Maven Central consumption with your current runtime.

Run Maven-Central consumption test:

```bash
mvn -Dtest=OnlineMavenBalanceIntegrationTest test
```

Run current-repo online API flow test (balance + upload type=0 + polling):

```bash
mvn -Dtest=OnlineGitBasicApiIntegrationTest test
```

Tip: if you use a local `.env`, run through the helper script:

```bash
./run-tests.sh -Dtest=OnlineMavenBalanceIntegrationTest test
./run-tests.sh -Dtest=OnlineGitBasicApiIntegrationTest test
```

**Note:** The helper script `run-tests.sh` automatically excludes Selenium tests by default when run without parameters.

### 8) Run CI pipeline locally (optional)

You can run the full GitLab CI pipeline locally using `gitlab-ci-local`:

```bash
# Install gitlab-ci-local (if not already installed)
npm install -g gitlab-ci-local

# Run the pipeline
gitlab-ci-local --file ./.gitlab-ci.yml
```

This will:
- Run tests on Java 17, 21, and 25 in parallel using Docker containers
- Calculate code coverage
- Generate test reports in `target/surefire-reports/`
- Generate JaCoCo coverage reports in `target/site/jacoco/`

**Requirements**: Docker must be installed and running.

**Note**: The pipeline loads credentials from `.env` file if present for integration tests.

## Common issues

- `401 Unauthorized`: verify username/password or token.
- Network timeout: confirm outbound connectivity to `api.dbcapi.me`.
- Java release error: run with `-Djava.release=17|21|25` based on your installed JDK.
- **Java version mismatch in Maven Central test**: The `OnlineMavenBalanceIntegrationTest` may fail with `class file has wrong version` if a previously published library on Maven Central was compiled with a newer Java version than your current runtime. Solution: run with Java 17/21/25 matching the published artifact baseline.
