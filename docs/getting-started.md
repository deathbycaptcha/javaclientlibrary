# Getting started

Guide to validate your environment compiles correctly and the SDK can authenticate successfully.

## Choose your path

- **Option A (recommended for consumers):** use Maven Central.
- **Option B (recommended for contributors):** use this Git repository source code.

## Option A: Use Maven Central (online)

If you want to consume the SDK from Maven Central (instead of source code in this repository), use:

- GroupId: `io.github.deathbycaptcha`
- ArtifactId: `deathbycaptcha-java-library`
- Version: `4.6.9`

`pom.xml` dependency:

```xml
<dependency>
  <groupId>io.github.deathbycaptcha</groupId>
  <artifactId>deathbycaptcha-java-library</artifactId>
  <version>4.6.9</version>
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

**Note:** Maven Central publishes only the core library artifact. Runnable samples live in this repository under `samples/src/main/java/examples/`.

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

**For running examples**: Use Maven profile `samples` (see section 4 and [samples usage](samples.md)).

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

All sample executions use profile `samples` (`-Psamples`) because examples are located outside the core source set.

`ExampleGetBalance` requires 3 command-line parameters:
1. Username
2. Password
3. API type: `HTTP` or `SOCKET`

Pass parameters using `-Dexec.args`:

```bash
mvn -Psamples exec:java -Dexec.mainClass="examples.ExampleGetBalance" \
  -Dexec.args="your_username your_password HTTP"
```

Or using Socket API:

```bash
mvn -Psamples exec:java -Dexec.mainClass="examples.ExampleGetBalance" \
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

### 6) Run other samples

Most examples require editing the source code to set credentials and captcha parameters. For more details on available samples and how to run them, see [Samples usage](samples.md).

### 7) Run CI pipeline locally (optional)

You can run the full GitLab CI pipeline locally using `gitlab-ci-local`:

```bash
# Install gitlab-ci-local (if not already installed)
npm install -g gitlab-ci-local

# Run the pipeline
gitlab-ci-local --file ./.gitlab-ci.yml
```

For more details, see [CI and coverage](ci-and-coverage.md).

## Common issues

- `401 Unauthorized`: Verify username/password or auth token in environment or `.env` file.
- Network timeout: Confirm outbound connectivity to `api.dbcapi.me`.
- Java release error: Use Java 17, 21, or 25 (see Requirements above).
- Samples not found: Use Maven profile `-Psamples` to include samples in compilation (see [Samples usage](samples.md)).
