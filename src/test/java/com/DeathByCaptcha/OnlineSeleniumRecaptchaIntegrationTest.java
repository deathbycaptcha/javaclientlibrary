package com.DeathByCaptcha;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OnlineSeleniumRecaptchaIntegrationTest {

    private static final String DEMO_URL = "https://www.google.com/recaptcha/api2/demo";

    @Test
    public void testSeleniumRecaptchaHeadlessFlow() throws Exception {
        Map<String, String> dotEnv = loadDotEnv();
        String authToken = readConfig("DBC_AUTHTOKEN", dotEnv);
        String username = readConfig("DBC_USERNAME", dotEnv);
        String password = readConfig("DBC_PASSWORD", dotEnv);

        boolean hasToken = authToken != null && !authToken.isEmpty();
        boolean hasUserPass = username != null && !username.isEmpty() && password != null && !password.isEmpty();
        Assume.assumeTrue(
            "Skipping Selenium integration test: set DBC_AUTHTOKEN or DBC_USERNAME/DBC_PASSWORD",
            hasToken || hasUserPass
        );

        ChromeOptions options = new ChromeOptions();
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin != null && !chromeBin.trim().isEmpty()) {
            options.setBinary(chromeBin.trim());
        }
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        Client client = null;

        try {
            driver.get(DEMO_URL);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            WebElement widget = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("recaptcha-demo")));
            String siteKey = widget.getDomAttribute("data-sitekey");
            assertNotNull("Expected reCAPTCHA site key", siteKey);
            assertFalse("Expected non-empty reCAPTCHA site key", siteKey.trim().isEmpty());

            client = hasToken ? new HttpClient(authToken) : new HttpClient(username, password);
            client.isVerbose = true;

            JSONObject params = new JSONObject();
            try {
                params.put("googlekey", siteKey);
                params.put("pageurl", DEMO_URL);
            } catch (JSONException e) {
                fail("Failed building captcha parameters: " + e.getMessage());
            }

            Captcha captcha = null;
            try {
                captcha = client.decode(params);
            } catch (IOException | InterruptedException e) {
                fail("Failed solving captcha with DBC: " + e.getMessage());
            }

            assertNotNull("Expected solved captcha", captcha);
            assertNotNull("Expected captcha text", captcha.text);
            assertFalse("Expected non-empty captcha text", captcha.text.trim().isEmpty());

            ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('g-recaptcha-response').value=arguments[0];",
                captcha.text
            );

            wait.until(d -> {
                WebElement field = d.findElement(By.id("g-recaptcha-response"));
                String value = field.getDomProperty("value");
                return value != null && !value.trim().isEmpty();
            });

            WebElement submitButton = driver.findElement(By.id("recaptcha-demo-submit"));
            submitButton.click();

            WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("recaptcha-success"))
            );

            String successText = successMessage.getText();
            assertNotNull("Expected success message text", successText);
            assertTrue(
                "Expected 'Verification Success' message after form submit, got: " + successText,
                successText.contains("Verification Success")
            );
        } finally {
            if (client != null) {
                client.close();
            }
            driver.quit();
        }
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Paths.get(".env");

        if (!Files.exists(envPath)) {
            return values;
        }

        try (BufferedReader reader = Files.newBufferedReader(envPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int splitIndex = trimmed.indexOf('=');
                if (splitIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, splitIndex).trim();
                String value = trimmed.substring(splitIndex + 1).trim();

                if ((value.startsWith("\"") && value.endsWith("\"")) ||
                    (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }

                values.put(key, value);
            }
        } catch (IOException ignored) {
        }

        return values;
    }

    private static String readConfig(String key, Map<String, String> dotEnv) {
        String fromEnv = System.getenv(key);
        if (fromEnv != null && !fromEnv.isEmpty()) {
            return fromEnv;
        }
        return dotEnv.get(key);
    }
}
