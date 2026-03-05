# Getting started

Guide to validate your environment compiles correctly and the SDK can authenticate successfully.

## 0) Use Maven Central (online)

If you want to consume the SDK from Maven Central (instead of source code in this repository), use:

- GroupId: `io.github.deathbycaptcha`
- ArtifactId: `deathbycaptcha-java-library`
- Version: `4.6.6`

`pom.xml` dependency:

```xml
<dependency>
  <groupId>io.github.deathbycaptcha</groupId>
  <artifactId>deathbycaptcha-java-library</artifactId>
  <version>4.6.6</version>
</dependency>
```

Quick online resolution check:

```bash
mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.7.1:get \
  -Dartifact=io.github.deathbycaptcha:deathbycaptcha-java-library:4.6.6 \
  -Dtransitive=false \
  -DremoteRepositories=central::default::https://repo1.maven.org/maven2
```

Expected result: `BUILD SUCCESS`.

## 1) Prepare your environment

- Java 25 LTS (recommended).
- Java 17 or 21 (supported for compatibility).
- Maven 3.9+.
- DeathByCaptcha credentials:
  - `DBC_USERNAME` and `DBC_PASSWORD`, or
  - `DBC_AUTHTOKEN`.

Example environment variables:

```bash
export DBC_USERNAME="your_username"
export DBC_PASSWORD="your_password"
# or
export DBC_AUTHTOKEN="your_token"
```

## 2) Compile the project

From the repository root:

```bash
mvn clean compile
```

## 3) Run the balance example

The fastest way to validate connectivity and authentication:

```bash
mvn exec:java -Dexec.mainClass="examples.ExampleGetBalance"
```

With valid credentials, you should see output similar to:

```text
Your balance is <amount> US cents
```

## 4) Run tests and coverage

```bash
mvn clean test
```

Generated artifacts:

- Test report: `target/surefire-reports/`
- JaCoCo coverage: `target/site/jacoco/`

## 5) Run a real captcha example

reCAPTCHA v2:

```bash
mvn exec:java -Dexec.mainClass="examples.ExampleRecaptchaV2"
```

Note: update credentials and parameters in the example before running it.

## 6) Validate online integration tests

These tests hit the real API and require valid credentials.

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

## Common issues

- `401 Unauthorized`: verify username/password or token.
- Network timeout: confirm outbound connectivity to `api.dbcapi.me`.
- Java release error: run with `-Djava.release=17|21|25` based on your installed JDK.
