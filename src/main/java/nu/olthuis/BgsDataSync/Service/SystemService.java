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

    //private final Map<String,JSONObject> systemsMap = new HashMap<>();

    private final EliteBgsConsumer eliteBgsConsumer;

    @Autowired
    public SystemService(EliteBgsConsumer eliteBgsConsumer) {
        this.eliteBgsConsumer = eliteBgsConsumer;
    }

    public void getEbgsSystemsMap(Map<String,JSONObject> nearbySystemsList){
        // no need for return value cause Map = object and variable is reference and input = output in this case
        nearbySystemsList.forEach((systemName,systemObject) -> {
            logger.debug("Getting data for: " + systemName);

            JSONObject ebgsSystemObject = getEbgsSystem(systemName);


            ebgsSystemObject.put("distance",systemObject.get("distance"));
            nearbySystemsList.put(systemName,ebgsSystemObject);
        });
        nearbySystemsList.forEach((systemName,system) -> logger.debug(system));
    }

    private JSONObject getEbgsSystem(String systemName) {

        JSONArray docsArray = null;

        try {
            String urlString = systemUri + URLEncoder.encode(systemName, "UTF-8");

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(urlString).build(true);

            URI uri = uriComponents.toUri();
            logger.debug("URI : " + uri);

            JSONObject ebgsDocObject = eliteBgsConsumer.getData(uri );
            logger.debug("SystemObject fetched from ebgs: " + ebgsDocObject);

            docsArray = (JSONArray) ebgsDocObject.get("docs");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return (JSONObject) docsArray.get(0);
    }

//    private void addSystemToDataSet(String systemName, JSONObject docObject) {
//
//        JSONArray docsArray = (JSONArray) docObject.get("docs");
//
//        //noinspection unchecked
//        for (JSONObject systemObject : (Iterable<JSONObject>) docsArray) {//System.out.println("ADDING: " + systemObject);
//
//            //noinspection unchecked
//            //systemObject.put("distance", calculateDistanceFromKolaga(systemObject));
//            systemsMap.put(systemName, systemObject);
//
//        }
//    }

//    private double getDoubleValue(Object coord) {
//
//        double value;
//        if (coord.getClass() == Long.class) {
//            value = ((Long) coord).doubleValue();
//        }
//        else {value = (double) coord;}
//
//        return value;
//    }

}
