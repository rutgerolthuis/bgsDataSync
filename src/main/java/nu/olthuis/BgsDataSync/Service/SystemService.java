package nu.olthuis.BgsDataSync.Service;

import nu.olthuis.BgsDataSync.Consumers.EliteBgsConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;

@Service
public class SystemService {
    private final static Logger logger = LogManager.getLogger(SystemService.class);

    private final static String systemUri = "https://elitebgs.kodeblox.com/api/ebgs/v4/systems?name=" ;

    private final Map<String,JSONObject> systemsMap = new HashMap<>();

    private final double kolagaX =  -32.93750;
    private final double kolagaY =  -14.21875;
    private final double kolagaZ =  -97.46875;
    private final EliteBgsConsumer eliteBgsConsumer;

    @Autowired
    public SystemService(EliteBgsConsumer eliteBgsConsumer) {
        this.eliteBgsConsumer = eliteBgsConsumer;
    }

    double getKolagaX() {
        return kolagaX;
    }
    double getKolagaY() {
        return kolagaY;
    }
    double getKolagaZ() {
        return kolagaZ;
    }

    public void buildSystemsMap(List<String> nearbySystemsList){
        for (String systemName : nearbySystemsList) {
            logger.debug("Getting data for: " + systemName);

            String urlString = "";
            try {
                 urlString = systemUri + URLEncoder.encode(systemName, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(urlString).build(true);

            URI uri = uriComponents.toUri();
            logger.debug("URI : " + uri);

            JSONObject systemObject = eliteBgsConsumer.getData(uri );
            logger.debug("SystemObject fetched : " + systemObject);
            addSystemToDataSet(systemName,systemObject);

        }

    }

    private void addSystemToDataSet(String systemName, JSONObject docObject) {

        JSONArray docsArray = (JSONArray) docObject.get("docs");

        //noinspection unchecked
        for (JSONObject systemObject : (Iterable<JSONObject>) docsArray) {//System.out.println("ADDING: " + systemObject);

            //noinspection unchecked
            systemObject.put("distance_from_kolaga", calculateDistanceFromKolaga(systemObject));
            systemsMap.put(systemName, systemObject);

        }
    }
    private double calculateDistanceFromKolaga(JSONObject systemObject) {

        double bx = getDoubleValue(systemObject.get("x")) ;
        double by = getDoubleValue(systemObject.get("y")) ;
        double bz = getDoubleValue(systemObject.get("z")) ;


        return CalculatorService.calculateDistance(kolagaX,kolagaY,kolagaZ,bx,by,bz);
    }

    public Map<String,JSONObject> getSystemsMap() {
        return systemsMap;
    }

    private double getDoubleValue(Object coord) {

        double value;
        if (coord.getClass() == Long.class) {
            value = ((Long) coord).doubleValue();
        }
        else {value = (double) coord;}

        return value;
    }

}
