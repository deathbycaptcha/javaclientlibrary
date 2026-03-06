package examples;

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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

public class ExampleSeleniumRecaptchaV2 {

    private static boolean isHeadlessEnabled() {
        String value = System.getProperty("selenium.headless", "true");
        return !"false".equalsIgnoreCase(value.trim());
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
        } catch (IOException e) {
            System.out.println("Failed reading .env: " + e.getMessage());
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

    public static void main(String[] args) {
        String baseUrl = "https://www.google.com/recaptcha/api2/demo";
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadlessEnabled()) {
            options.addArguments("-headless");
        }

        WebDriver driver = new FirefoxDriver(options);
        Map<String, String> dotEnv = loadDotEnv();

        String authToken = readConfig("DBC_AUTHTOKEN", dotEnv);
        String username = readConfig("DBC_USERNAME", dotEnv);
        String password = readConfig("DBC_PASSWORD", dotEnv);

        try {
            driver.get(baseUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("recaptcha-demo")));

            String googleKey = element.getDomAttribute("data-sitekey");
            System.out.println("Google key: " + googleKey);

            Client client;
            if (authToken != null && !authToken.isEmpty()) {
                client = new HttpClient(authToken);
            } else if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                client = new HttpClient(username, password);
            } else {
                System.out.println("Missing credentials. Set DBC_AUTHTOKEN or DBC_USERNAME/DBC_PASSWORD in env or .env");
                return;
            }

            client.isVerbose = true;

            try {
                System.out.println("Your balance is " + client.getBalance() + " US cents");
            } catch (Exception e) {
                System.out.println("Failed fetching balance: " + e.toString());
                return;
            }

            Captcha captcha = null;
            JSONObject json_params = new JSONObject();
            try {
                json_params.put("googlekey", googleKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json_params.put("pageurl", baseUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                captcha = client.decode(json_params);
            } catch (IOException | InterruptedException e) {
                System.out.println(e.getMessage());
                System.out.println("Failed uploading CAPTCHA");
            }

            if (captcha == null) {
                System.out.println("No captcha solution (maybe implement retry)...closing");
                return;
            }

            ((JavascriptExecutor)driver).executeScript(
                "document.getElementById('g-recaptcha-response').value=arguments[0];",
                captcha.text
            );

            WebElement responseField = driver.findElement(By.id("g-recaptcha-response"));
            String injectedToken = responseField.getDomProperty("value");
            if (injectedToken == null || injectedToken.trim().isEmpty()) {
                System.out.println("Failed injecting reCAPTCHA token into form");
                return;
            }

            WebElement button = driver.findElement(By.id("recaptcha-demo-submit"));
            button.click();

            try {
                WebElement success_element = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.className("recaptcha-success"))
                );
                System.out.println(success_element.getText());
            } catch (Exception e) {
                System.out.printf("Success message not found: %s%n", e.getMessage());
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            driver.quit();
        }
    }
}
