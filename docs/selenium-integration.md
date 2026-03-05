# Selenium integration

Practical guide to automate reCAPTCHA v2 using Selenium + Java SDK.

Reference implementation: `src/main/java/examples/ExampleSeleniumRecaptchaV2.java`.

## Requirements

- Java 17+.
- Maven 3.9+.
- Compatible browser and driver (Chrome/ChromeDriver or Firefox/GeckoDriver).
- DeathByCaptcha credentials.

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

Client client = new HttpClient("DBC_USERNAME", "DBC_PASSWORD");
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

## Selenium best practices

- Use `WebDriverWait` for dynamic element loading.
- Verify token injection before submitting the form.
- Close the driver in a `finally` block to avoid orphan processes.
- For CI pipelines, run in headless mode and use preinstalled drivers.

## Common errors

- `NoSuchElementException`: wrong selector or incomplete page load.
- Driver mismatch: incompatible browser and driver versions.
- Captcha `null`: implement retries with higher timeout.
