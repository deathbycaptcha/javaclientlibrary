package examples;

import java.io.IOException;

import org.json.JSONObject;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.SocketClient;

public class ExampleSiara {
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
                // Proxy and cyber siara data
                String proxy = "";
                String proxytype = "";
                String slideurlid = "OXR2LVNvCuXykkZbB8KZIfh162sNT8S2";
                String pageurl = "https://www.cybersiara.com/book-a-demo";
                String useragent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36";
                /* Upload a cyber siara and poll for its status with 120 seconds timeout.
                   Put your proxy, proxy type, page slideurlid, page url and user agent */
                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("slideurlid", slideurlid);
                json_params.put("pageurl", pageurl);
                json_params.put("useragent", useragent);
                captcha = client.decode(17, json_params);
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
