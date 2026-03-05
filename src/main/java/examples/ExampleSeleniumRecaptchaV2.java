package examples;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.time.Duration;

public class ExampleSeleniumRecaptchaV2 {

    public static void main(String[] args) {
        String baseUrl = "https://www.google.com/recaptcha/api2/demo";
        //WebDriver driver = new FirefoxDriver();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(baseUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("recaptcha-demo")));

            String googleKey = element.getDomAttribute("data-sitekey");
            System.out.println("Google key: " + googleKey);

            Client client = new HttpClient("DBC_USERNAME", "DBC_PASSWORD");
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

            String script = String.format("document.getElementById('g-recaptcha-response').value='%s'", captcha.text);
            ((JavascriptExecutor)driver).executeScript(script);

            WebElement button = driver.findElement(By.id("recaptcha-demo-submit"));
            button.click();

            try {
                WebElement success_element = driver.findElement(By.className("recaptcha-success"));
                System.out.println(success_element.getText());
            } catch (Exception e) {
                System.out.printf("Success message not found: %s%n", e.getMessage());
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        driver.close();
    }
}
