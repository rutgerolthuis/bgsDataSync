package nu.olthuis.BgsDataSync;


import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class Application 
{
    //private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public static void main( String[] args )
    {
        if (args.length > 0 && args[0].equals("shutdown")) {
            System.out.println("we're in... " + args[0]);
            URL url = null;
            try {
                url = new URL("http://localhost:8080/actuator/shutdown");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                int status = con.getResponseCode();
                System.out.println("shutting down " + status);
                con.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {

            SpringApplication.run(Application.class, args);
        }
    }
}

// https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html
// for logging