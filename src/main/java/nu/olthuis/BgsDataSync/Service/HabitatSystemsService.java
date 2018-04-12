package nu.olthuis.BgsDataSync.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class HabitatSystemsService {
    private final static Logger logger = LogManager.getLogger(HabitatSystemsService.class);

    private final double KolagaX;
    private final double KolagaY;
    private final double KolagaZ;

    @Autowired
    public HabitatSystemsService(SystemService systemService) {
        this.KolagaX = systemService.getKolagaX();
        this.KolagaY = systemService.getKolagaY();
        this.KolagaZ = systemService.getKolagaZ();
    }

    public List<String> getNearbySystems(int referenceDistance) {

        return getNearbySystems("",referenceDistance);
    }
    @SuppressWarnings("unchecked")
    private List<String> getNearbySystems(String referenceSystem, int referenceDistance) {

        logger.info("Getting all systems within " + referenceDistance + " from the reference system.");

        JSONArray systemsArray = getHabitatSystemsFromBigFile();
        List<String> nearbySystemsList = new ArrayList<>();

        Objects.requireNonNull(systemsArray).forEach(system -> {

            JSONObject systemObject = (JSONObject) system;
            double distance = calculateDistanceFromKolaga(systemObject);

            if (distance <= referenceDistance ) {
                logger.debug("Adding system: " + systemObject.get("name") + " which is " + distance + " ly from Kolaga.");
                logger.debug("x: " + systemObject.get("x") + " y: " + systemObject.get("y")  + " z: " + systemObject.get("z") );
                nearbySystemsList.add(systemObject.get("name").toString());
            }
        });

        logger.debug("Added " + nearbySystemsList.size() + " systems to the nearby list.");

        return nearbySystemsList;
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

    private double calculateDistanceFromKolaga (JSONObject system) {

        try {

            double x = getDoubleValue(system.get("x"));
            double y = getDoubleValue(system.get("y"));
            double z = getDoubleValue(system.get("z"));

            return CalculatorService.calculateDistance(KolagaX,KolagaY,KolagaZ, x, y, z);

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
