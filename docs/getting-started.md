# Getting started

Guide to validate your environment compiles correctly and the SDK can authenticate successfully.

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

## Common issues

- `401 Unauthorized`: verify username/password or token.
- Network timeout: confirm outbound connectivity to `api.dbcapi.me`.
- Java release error: run with `-Djava.release=17|21|25` based on your installed JDK.
