package examples;

import java.io.IOException;

import com.DeathByCaptcha.Client;
import com.DeathByCaptcha.HttpClient;
import com.DeathByCaptcha.SocketClient;


public class ExampleGetBalance
{
    public static void main(String[] args)
        throws Exception
    {

        System.out.println(args.length);
        if(args.length == 3){
            System.out.println("Getting balance");
        }else{
            System.out.println("Wrong number of arguments");
            System.out.println("You must use username/password, HTTP/SOCKET combination");
            System.exit(-1);
        }
        Client client;

        if(args[2].equals("HTTP")){
            // using http API
            System.out.println("Using HTTP API");
            client = (Client)(new HttpClient(args[0], args[1]));
        }else{
            // using sockets API
            System.out.println("Using SOCKETS API");
            client = (Client)(new SocketClient(args[0], args[1]));
        }

        client.isVerbose = true;

        try {
            try {
                System.out.println("Your balance is " + client.getBalance() + " US cents");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Failed fetching balance: " + e.toString());
                System.exit(-1);
                return;
            }

        } catch (com.DeathByCaptcha.Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }
}
