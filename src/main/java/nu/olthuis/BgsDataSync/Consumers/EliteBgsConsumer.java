package nu.olthuis.BgsDataSync.Consumers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@Service
public class EliteBgsConsumer {
    //private JSONArray systemArray = new JSONArray();
    private final static Logger logger = LogManager.getLogger(EliteBgsConsumer.class);




    public JSONObject getData(URI uri )  {
        logger.info("Data request received for : " + uri );

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(result);
            return (JSONObject) obj;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;


    }


//    public JSONObject getSystems() {
//        final List<String> systemList = new ArrayList();
//        systemList.add("Kolaga");
//        systemList.add("Bestakas");
//        systemList.forEach(system -> {JSONObject systemObject = getSystem(system);
//            addSystem(systemObject);
//        } );
//
//        JSONObject systemData = new JSONObject();
//        systemData.put("systems",systemArray);
//        return systemData;
//    }
//
//    private void addSystem (JSONObject system) {
//
//        if (systemArray == null) {
//            System.out.println("Initialize systemData");
//            systemArray = new JSONArray();
//
//        }
//
//        JSONArray docsArray = (JSONArray) system.get("docs");
//        System.out.println(docsArray.toString());
//        Iterator<JSONObject> docsIterator = docsArray.iterator();
//        while(docsIterator.hasNext()) {
//            JSONObject systemObject = docsIterator.next();
//            System.out.println("ADDING: " + systemObject);
//            systemArray.add(systemObject);
//        }
//
//    }

}
//https://elitebgs.kodeblox.com/api/ebgs/v4/systems?name=<faction name>