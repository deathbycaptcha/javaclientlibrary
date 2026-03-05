package examples;

import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

import java.io.IOException;

class ExampleTencent {
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
                // Proxy and tencent data
                String proxy = "";
                String proxytype = "";
                String appid = "2044554539";
                String pageurl = "https://example.com";
                /* Upload a tencent and poll for its status with 120 seconds timeout.
                   Put your proxy, proxy type, page appid, page url and solving timeout (in seconds)
                   0 or nothing for the default timeout value. */
                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("appid", appid);
                json_params.put("pageurl", pageurl);
                captcha = client.decode(23, json_params);
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
