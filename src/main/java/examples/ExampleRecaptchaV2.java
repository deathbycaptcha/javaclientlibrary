package examples;

import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

import java.io.IOException;

public class ExampleRecaptchaV2 {
    public static void main(String[] args) throws Exception {

        // Put your DBC username & password or authtoken here:
        String username = "your_username_here";
        String password = "your_password_here";
        String authtoken = "your_authtoken_here";

        // Death By Captcha Socket Client
        Client client = (Client) (new SocketClient(username, password));
        // Death By Captcha http Client
        // Client client = (Client) (new HttpClient(username, password));
        client.isVerbose = true;

        /* Using authtoken
           Client client = (Client) new HttpClient(authtoken); */

        try {
            try {
                System.out.println("Your balance is " + client.getBalance() + " US cents");
            } catch (IOException e) {
                System.out.println("Failed fetching balance: " + e.toString());
                return;
            }

            Captcha captcha = null;
            try {
                // Proxy and reCAPTCHA v2 token data
                // String proxy = "http://user:password@127.0.0.1:1234";
                // String proxytype = "http";
                String proxy = "";
                String proxytype = "";
                String googlekey = "6LfW6wATAAAAAHLqO2pb8bDBahxlMxNdo9g947u9";
                String pageurl = "https://recaptcha-demo.appspot.com/recaptcha-v2-checkbox.php";
                /* Upload a reCAPTCHA v2 and poll for its status with 120 seconds timeout.
                   Put the token params and timeout (in seconds)
                   0 or nothing for the default timeout value. */
                captcha = client.decode(4, proxy, proxytype, googlekey, pageurl);

            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA: " + e.toString());
                return;
            }

            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved");

                /* Report incorrectly solved CAPTCHA if necessary.
                   Make sure you've checked if the CAPTCHA was in fact incorrectly
                   solved, or else you might get banned as abuser. */
                /*try {
                    if (client.report(captcha)) {
                        System.out.println("Reported as incorrectly solved");
                    } else {
                        System.out.println("Failed reporting incorrectly solved CAPTCHA");
                    }
                } catch (IOException e) {
                    System.out.println("Failed reporting incorrectly solved CAPTCHA: " + e.toString());
                }*/

            } else {
                System.out.println("Failed solving CAPTCHA");
            }
        } catch (com.DeathByCaptcha.Exception e) {
            e.printStackTrace();
        }
    }
}
