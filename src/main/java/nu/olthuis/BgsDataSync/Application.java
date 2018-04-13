package nu.olthuis.BgsDataSync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


@SpringBootApplication
public class Application 
{
    public static void main( String[] args ) {

        if (args.length > 0 && args[0].equals("shutdown")) {

            System.out.println("we're in... " + args[0]);

            int portNumber = 8080;

            if (args.length == 2){portNumber = Integer.parseInt(args[1].split("=")[1]);}

            String shutdownUrl = "http://localhost:" + portNumber + "/actuator/shutdown";
            try {
                URL url = new URL(shutdownUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.getResponseCode();
                System.out.println("shutting down app at" + shutdownUrl);
                con.disconnect();
                System.out.println("Shutdown successful");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Unable to reach bgs data app..  incorrect port or app is already stopped.");
            }
        }
        else {
            SpringApplication.run(Application.class, args);
        }
    }
}

// https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html
// for logging