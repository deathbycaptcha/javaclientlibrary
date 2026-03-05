package examples;

import com.DeathByCaptcha.AccessDeniedException;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;
import com.DeathByCaptcha.Captcha;

import java.io.IOException;

public class ExampleRecaptchaImageGroup {
    public static void main(String[] args)
            throws Exception {

        // Put your DBC username & password or authtoken here:
        String username = "your_username_here";
        String password = "your_password_here";
        String authtoken = "your_authtoken_here";

        String filename = "src/images/test2.jpg";
        String banner = "src/images/banner.jpg";
        String banner_text = "choose all pizza:";

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
                /* Upload a CAPTCHA and poll for its status with 120 seconds timeout.
                   Put you CAPTCHA image file name, file object, input stream, or
                   vector of bytes, and solving timeout (in seconds) if 0 the default value take place.
                   please note we are specifying banner, banner_test and type=3 in the second argument */
                captcha = client.decode(filename, 3, banner, banner_text, 0);
                /* you can supply optional `grid` argument to decode() call, with a
                  string like 3x3 or 2x4, defining what grid individual images were located at

                  example:
                    captcha = client.decode(filename, 3, banner, banner_text, "2x4", 0);

                  see 2x4.png example image to have an idea what that images look like
                  If you wont supply `grid` argument, dbc will attempt to autodetect the grid */

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
