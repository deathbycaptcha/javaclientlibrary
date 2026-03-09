package examples;

import java.io.IOException;

import org.json.JSONObject;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;

public class ExampleAmazonWaf {
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
                // Proxy and Amazon Waf data
                String proxy = "";
                String proxytype = "";
                String sitekey="AQIDAHjyBMeDXXc3z2ZxmD9jsQ8eNMGHqeii56iL2Guh4A==";
                String pageurl = "https://efw47fpad9.execute-api.us-east-1.amazonaws.com/latest";
                String iv = "CgAFRjIw2vAAABSM";
                String context = "zPT0jOl1rQlUN";
                //String challengejs = ""; // optional parameter
                //String captchajs = "";   // optional parameter
                /* Upload an Amazon Waf and poll for its status with 120 seconds timeout.
                   Put your proxy, proxy type, page sitekey, page url and solving timeout (in seconds)
                   0 or nothing for the default timeout value. */

                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("sitekey", sitekey);
                json_params.put("pageurl", pageurl);
                json_params.put("iv", iv);
                json_params.put("context", context);
                // json_params.put("challengejs", challengejs);  // optional parameter
                // json_params.put("captchajs", captchajs);      // optional parameter
                captcha = client.decode(16, json_params);

            } catch (IOException e) {
                System.out.println("Failed uploading CAPTCHA");
                return;
            }
            if (null != captcha) {
                System.out.println("CAPTCHA " + captcha.id + " solved: " + captcha.text);

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
