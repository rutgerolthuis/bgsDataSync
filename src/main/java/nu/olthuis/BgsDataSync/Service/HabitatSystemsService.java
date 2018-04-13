package nu.olthuis.BgsDataSync.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.*;

@Service
public class HabitatSystemsService {
    private final static Logger logger = LogManager.getLogger(HabitatSystemsService.class);

    private double referenceX;
    private double referenceY;
    private double referenceZ;

    @SuppressWarnings("unchecked")
    public Map<String,JSONObject> getNearbySystems(String referenceSystem, int referenceDistance) {

        logger.info("Getting all systems within " + referenceDistance + " from " + referenceSystem + " system.");
        Map<String, JSONObject> habitatSystemsMap = createSystemMap(referenceSystem);

        return getSystemsNearReferenceSystem(habitatSystemsMap,referenceSystem,referenceDistance);

    }

    private Map<String,JSONObject> getSystemsNearReferenceSystem(Map<String,JSONObject> habitatSystemsMap, String referenceSystem, int referenceDistance) {

        Double distance;
        Iterator<String> it = habitatSystemsMap.keySet().iterator();
        while (it.hasNext()) {
            String systemName = it.next();
            JSONObject systemObject = habitatSystemsMap.get(systemName);
            distance = calculateDistanceFromReferenceSystem(systemObject);
            if (distance <=  referenceDistance) {
                logger.debug("Adding system: " + systemObject.get("name") + " which is " + distance + " ly from " + referenceSystem);
                logger.debug("x: " + systemObject.get("x") + " y: " + systemObject.get("y")  + " z: " + systemObject.get("z") );
                systemObject.put("distance",distance);
            }
            else {it.remove();}
        }

        logger.debug("Added " + habitatSystemsMap.size() + " systems to the nearby list.");

        return habitatSystemsMap;
    }

    private Map createSystemMap(String referenceSystem) {
        JSONArray systemsArray = getHabitatSystemsFromBigFile();

        Map<String,JSONObject> habitatSystemsMap = new HashMap<>();

        Objects.requireNonNull(systemsArray).forEach(system -> {

            JSONObject systemObject = (JSONObject) system;
            String systemName =  systemObject.get("name").toString();

            if (systemName.equals(referenceSystem)) {

                referenceX = getDoubleValue(systemObject.get("x"));
                referenceY = getDoubleValue(systemObject.get("y"));
                referenceZ = getDoubleValue(systemObject.get("z"));
            }

            habitatSystemsMap.put(systemName,systemObject);

        });

        return habitatSystemsMap;
    }

    private JSONArray getHabitatSystemsFromBigFile() {

        try {
            URL loadedResource = this.getClass().getClassLoader().getResource("systems_populated.json");
            InputStream inputStream = loadedResource.openStream();
            JSONParser parser = new JSONParser();
            return (JSONArray) parser.parse(new InputStreamReader(inputStream, "UTF-8"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private double calculateDistanceFromReferenceSystem (JSONObject system) {

        try {

            double x = getDoubleValue(system.get("x"));
            double y = getDoubleValue(system.get("y"));
            double z = getDoubleValue(system.get("z"));

            return CalculatorService.calculateDistance(referenceX,referenceY,referenceZ, x, y, z);

        } catch (Exception e) {
            throw e;
        }
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
