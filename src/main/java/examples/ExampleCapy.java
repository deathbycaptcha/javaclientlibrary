package examples;

import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;
import org.json.JSONObject;

import java.io.IOException;

class ExampleCapy {
    public static void main(String[] args)
            throws Exception {

        // Put your DBC username & password or authtoken here:
        String username = "your_username_here";
        String password = "your_password_here";
        String authtoken = "your_authtoken_here";

        /* Death By Captcha Socket Client
           Client client = (Client) (new SocketClient(username, password));
           Death By Captcha http Client */
        Client client = (Client) (new HttpClient(username, password));
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
                // Proxy and data
                String proxy = "http://user:password@127.0.0.1:1234";
                String proxytype = "http";
                String captchakey = "PUZZLE_ADJmfwm3ff32mcwdiem3vw";
                String pageurl = "https://client-demo.testsite.com/test-capy";
                String api_server = "https://client-demo.testsite.com/";

                //Send a json with the parameters
                JSONObject json_params = new JSONObject();
                json_params.put("proxy", proxy);
                json_params.put("proxytype", proxytype);
                json_params.put("captchakey", captchakey);
                json_params.put("api_server", api_server);
                json_params.put("pageurl", pageurl);
                captcha = client.decode(15, json_params);
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
