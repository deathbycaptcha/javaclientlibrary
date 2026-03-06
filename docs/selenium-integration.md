# Selenium integration

Practical guide to automate reCAPTCHA v2 using Selenium + Java SDK.

Reference implementation: `src/main/java/examples/ExampleSeleniumRecaptchaV2.java`.

## Requirements

- Java 17+.
- Maven 3.9+.
- Compatible browser and driver (Chrome/ChromeDriver or Firefox/GeckoDriver).
- DeathByCaptcha credentials in environment or local `.env`.

## Local credentials with `.env`

Create a `.env` file in the project root (same level as `pom.xml`) and set either auth token or username/password:

```dotenv
DBC_USERNAME=your_username
DBC_PASSWORD=your_password
# or
DBC_AUTHTOKEN=your_auth_token
```

`ExampleSeleniumRecaptchaV2` reads first from system environment, then from `.env`.

## Workflow

1. Open a page with reCAPTCHA.
2. Extract the widget `data-sitekey`.
3. Send `googlekey` + `pageurl` to the SDK.
4. Inject the solved token into `g-recaptcha-response`.
5. Submit the form and validate the result.

## Minimal example

```java
import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

WebDriver driver = new ChromeDriver();
String pageUrl = "https://www.google.com/recaptcha/api2/demo";

driver.get(pageUrl);
WebElement widget = driver.findElement(By.id("recaptcha-demo"));
String siteKey = widget.getDomAttribute("data-sitekey");

String username = System.getenv("DBC_USERNAME");
String password = System.getenv("DBC_PASSWORD");
Client client = new HttpClient(username, password);
JSONObject params = new JSONObject()
    .put("googlekey", siteKey)
    .put("pageurl", pageUrl);

Captcha captcha = client.decode(params);
if (captcha != null) {
    ((JavascriptExecutor) driver).executeScript(
        "document.getElementById('g-recaptcha-response').value=arguments[0];",
        captcha.text
    );
}
```

## Execution

```bash
mvn compile
mvn exec:java -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2"
```

## Headless mode

`ExampleSeleniumRecaptchaV2` reads the system property `selenium.headless`.

- Default behavior: if not set, it runs in headless mode (`true`).
- Set `-Dselenium.headless=true` to force headless mode.
- Set `-Dselenium.headless=false` to open Chrome with UI.

Run explicitly in headless mode:

```bash
mvn exec:java \
    -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2" \
    -Dselenium.headless=true
```

Run with browser UI locally:

```bash
mvn exec:java \
    -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2" \
    -Dselenium.headless=false
```

If `.env` is present, no extra export step is required.

If ChromeDriver is not on your PATH, run with explicit driver path:

```bash
mvn exec:java \
    -Dexec.mainClass="examples.ExampleSeleniumRecaptchaV2" \
    -Dselenium.headless=true \
    -Dwebdriver.chrome.driver="/absolute/path/to/chromedriver"
```

## Selenium best practices

- Use `WebDriverWait` for dynamic element loading.
- Verify token injection before submitting the form.
- Close the driver in a `finally` block to avoid orphan processes.
- For CI pipelines, run in headless mode and use preinstalled drivers.

## CI validation

GitHub Actions includes an independent workflow for this scenario:

- Workflow: `.github/workflows/selenium-integration-tests.yml`
- Test class: `src/test/java/com/DeathByCaptcha/OnlineSeleniumRecaptchaIntegrationTest.java`
- It validates solve + token injection + form post success message on the Google demo page.

## Common errors

- `NoSuchElementException`: wrong selector or incomplete page load.
- Driver mismatch: incompatible browser and driver versions.
- Captcha `null`: implement retries with higher timeout.
