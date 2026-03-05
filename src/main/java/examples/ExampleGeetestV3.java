package examples;

import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

import java.io.IOException;

class ExampleGeetestV3 {
    public static void main(String[] args)
            throws Exception {

        // Put your DBC username & password or authtoken here:
        String username = "your_username_here";
        String password = "your_password_here";
        String authtoken = "your_authtoken_here";

        //Death By Captcha Socket Client
        Client client = (Client) (new SocketClient(username, password));
        //Death By Captcha http Client
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
                // Proxy and geetest data
                String proxy = "";
                String proxytype = "";
                String gt = "022397c99c9f646f6477822485f30404";
                String challenge = "514a40a580d49a00274cd1523a045f92";
                String pageurl = "https://www.geetest.com/en/demo";

                // IMPORTANT: challenge parameter changes everytime
                // target site realoads the page
                // in this case we can see parameters here
                // https://www.geetest.com/demo/gt/register-enFullpage-official?t=1664547919370
                // just in this case, every site is different
                // we must examine the api calls to geetest to get the challenge

                /* Upload a geetest and poll for its status with 120 seconds timeout.
                   Put your proxy, proxy type, page sitekey, page url and solving timeout (in seconds)
                   0 or nothing for the default timeout value. */

                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("gt", gt);
                json_params.put("challenge", challenge);
                json_params.put("pageurl", pageurl);

                captcha = client.decode(8, json_params);

            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }
            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

                // // To access the response by item
                // JSONObject text = new JSONObject(captcha.text);
                // System.out.println("challenge: " + text.get("challenge"));
                // System.out.println("validate: " + text.get("validate"));
                // System.out.println("seccode: " + text.get("seccode"));

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
            System.out.println(e);
        }


    }
}
