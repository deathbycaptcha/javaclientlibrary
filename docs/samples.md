# Samples usage

This guide explains how to run the repository samples after they were moved out of the core library sources.

## Where samples live

Runnable samples are in:

- `samples/src/main/java/examples/`

The Maven Central artifact (`io.github.deathbycaptcha:deathbycaptcha-java-library`) contains only the core library.

If you only import the Maven dependency in another project, `examples.*` classes are not available there.

## Build modes

- Core library only (default):

```bash
mvn clean compile
```

- Core library + samples (enable samples profile):

```bash
mvn -Psamples clean compile
```

## Run a sample

Use `-Psamples` whenever you run a class from `examples.*`:

```bash
mvn -Psamples exec:java \
  -Dexec.mainClass="examples.ExampleGetBalance" \
  -Dexec.args="your_username your_password HTTP"
```

Socket API variant:

```bash
mvn -Psamples exec:java \
  -Dexec.mainClass="examples.ExampleGetBalance" \
  -Dexec.args="your_username your_password SOCKET"
```

## Run Selenium sample

```bash
mvn -Psamples exec:java \
  -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2" \
  -Dselenium.headless=true
```

## Common pitfalls

- If you omit `-Psamples`, `examples.*` classes are not added to Maven sources and execution fails.
- Editing samples does not change the published Maven Central artifact unless you also change core classes.
- For production integrations, consume the core library API (`com.DeathByCaptcha.*`) instead of invoking sample classes.
