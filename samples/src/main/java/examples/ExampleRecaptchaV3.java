package examples;

import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

import java.io.IOException;

public class ExampleRecaptchaV3 {
    public static void main(String[] args)
            throws Exception {

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
                /* Proxy and reCAPTCHA v3 token data
                   - reCAPTCHA v3 requires 'action' that is the action that triggers
                   reCAPTCHA v3 validation
                   - if 'action' isn't provided we use the default value "verify"
                   - also you need to provide 'min_score', a number from 0.1 to 0.9,
                   this is the minimum score acceptable from recaptchaV3 */
                // String proxy = "http://user:password@127.0.0.1:1234";
                // String proxytype = "http";
                String proxy = "";
                String proxytype = "";
                String googlekey = "6LdyC2cUAAAAACGuDKpXeDorzUDWXmdqeg-xy696";
                String pageurl = "https://recaptcha-demo.appspot.com/recaptcha-v3-request-scores.php";
                String action = "examples/v3scores";
                double min_score = 0.3;
                /* Upload a reCAPTCHA v3 and poll for its status with 120 seconds timeout.
                   Put the token params and timeout (in seconds)
                   0 or nothing for the default timeout value.  */
                captcha = client.decode(proxy, proxytype, googlekey, pageurl, action, min_score);

                //other method is to send a json with the parameters
                /*
                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("googlekey", googlekey);
                json_params.put("pageurl", pageurl);
                json_params.put("action", action);
                json_params.put("min_score", min_score);
                captcha = client.decode(5, json_params);
                 */
            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }

            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

                // Report incorrectly solved CAPTCHA if necessary.
                // Make sure you've checked if the CAPTCHA was in fact incorrectly
                // solved, or else you might get banned as abuser.
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
            System.out.println(e);
        }


    }
}
