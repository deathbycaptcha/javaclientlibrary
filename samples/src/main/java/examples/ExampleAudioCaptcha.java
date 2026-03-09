package examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;

public class ExampleAudioCaptcha {
    public static void main(String[] args)
            throws Exception {

        // Put your DBC username & password or authtoken here:
        String username = "your_username_here";
        String password = "your_password_here";
        String authtoken = "your_authtoken_here";

        // The path to the audio file
        String filePath = "images/audio.mp3";
        String encodedString = null;

        // Read the audio file and encode the file to base64
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            encodedString = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String audio = encodedString;
        String language = "en";

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
                   Put you CAPTCHA audio file base64 encoded, the language and solving
                   timeout (in seconds) if 0 the default value take place.
                   please note we are specifying type=13 */
                captcha = client.decode(13, audio, language);
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
