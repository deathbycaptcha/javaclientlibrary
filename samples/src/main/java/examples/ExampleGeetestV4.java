package examples;

import java.io.IOException;

import org.json.JSONObject;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;

public class ExampleGeetestV4 {
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
                String captcha_id = "fcd636b4514bf7ac4143922550b3008b";
                String pageurl = "https://www.geetest.com/en/adaptive-captcha-demo";
                /* Upload a geetest and poll for its status with 120 seconds timeout.
                   Put your proxy, proxy type, page sitekey, page url and solving timeout (in seconds)
                   0 or nothing for the default timeout value. */

                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("captcha_id", captcha_id);
                json_params.put("pageurl", pageurl);

                captcha = client.decode(9, json_params);

            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }
            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

                // // To access the response by item
                // JSONObject text = new JSONObject(captcha.text);
                // System.out.println("captcha_id: " + text.get("captcha_id"));
                // System.out.println("lot_number: " + text.get("lot_number"));
                // System.out.println("pass_token: " + text.get("pass_token"));
                // System.out.println("gen_time: " + text.get("gen_time"));
                // System.out.println("captcha_output: " + text.get("captcha_output"));

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
