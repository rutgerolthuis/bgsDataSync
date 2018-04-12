package nu.olthuis.BgsDataSync.Service;

import nu.olthuis.BgsDataSync.Translators.FactionsFromSystemsExtractor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BGSSyncService {

    private final static Logger logger = LogManager.getLogger(BGSSyncService.class);

    private final SystemService systemService;
    private final FactionService factionService;
    private final HabitatSystemsService habitatSystemsService;

    private Map<String,JSONObject> systemsMap;
    private Map<String,JSONObject> factionsMap;
    private List<String> nearbySystemList;
    private Set<String> factionSet = new HashSet<>();
    int myCounter = 0;
    @Autowired
    public BGSSyncService (SystemService systemService, FactionService factionService, HabitatSystemsService habitatSystemsService) {

        this.systemService = systemService;
        this.factionService = factionService;
        this.habitatSystemsService = habitatSystemsService;
    }


    public JSONObject getBgsData(int range) {

        // this is the main procedure. The driver of them all
        // 1. get system list (which systems do we want to get data from?
        // 2. get the system data for all the systems in the list
        // 3. get faction data (get the factions from the system lists and gather their data
        // 4. merge all into one big momma json object
        logger.info("Data sync request started for all systems within " + range + " ly from Kolaga.");
        fillNearbySystems(range);
        logger.debug("Fill Nearby Systems done");
        buildSystemData();
        logger.debug("Build system data done");
        buildFactionData();
        logger.debug("Faction data build");
        insertFactionDataInSystems();
        logger.debug("Factions inserted into systems");
        logger.info("Data sync request finished..");

        return mergeSystems();

    }

    private void fillNearbySystems(int referenceDistance) {

        nearbySystemList = habitatSystemsService.getNearbySystems(referenceDistance);
    }

    @SuppressWarnings("unchecked")
    private void insertFactionDataInSystems() {
        logger.info("Processing " + factionsMap.size() + " factions");
        factionsMap.forEach((factionName,factionObject) -> {

            String dateUpdated = (String) factionObject.get("updated_at");
            String government = (String) factionObject.get("government");
            String allegiance = (String) factionObject.get("allegiance");

            JSONArray factionPresence = (JSONArray) factionObject.get("faction_presence");
            JSONArray factionHistory = (JSONArray) factionObject.get("history");
            Iterator<?> factionPresenceIterator = factionPresence.iterator();

            factionPresenceIterator.forEachRemaining(localFactionPresence -> {

                String systemNameForLocalFaction = (String) ((JSONObject) localFactionPresence).get("system_name");
                logger.debug("Trying to add the faction info for system: " + systemNameForLocalFaction +  " and faction: " + factionName );

                JSONObject systemObject = systemsMap.get(systemNameForLocalFaction);
                if (systemObject == null) {
                    // very well possible that the faction is in a system we do not watch...
                    logger.debug("No system object found for system: " +  systemNameForLocalFaction + "; skipping.");
                    return;
                }

                JSONArray factionsFromSystem = (JSONArray) systemObject.get("factions");
                Iterator<?> factionsFromSystemIterator = factionsFromSystem.iterator();

                factionsFromSystemIterator.forEachRemaining(systemFaction -> {
                    myCounter++;
                    if (((JSONObject) systemFaction).get("name").toString().equals(factionName)) {

                        logger.debug("Writing localFactionPresence and history for faction : " + factionName + " in system " + systemNameForLocalFaction);
                        ((JSONObject)systemFaction).put("updated_at",dateUpdated);
                        ((JSONObject)systemFaction).put("government",government);
                        ((JSONObject)systemFaction).put("allegiance",allegiance);

                        ((JSONObject)systemFaction).put("presence",localFactionPresence);

                        ((JSONObject) systemFaction).put("history",factionHistory);

                    }

                });

            });

        });
    }

    @SuppressWarnings("unchecked")
    private JSONObject mergeSystems() {

        JSONArray systemsArray = new JSONArray();

        systemsMap.forEach((sysName, systemObject) -> systemsArray.add( systemObject));

        JSONObject bgsDataOutput = new JSONObject();
        Date date = new Date();

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        bgsDataOutput.put("time", sfd.format(date) );
        bgsDataOutput.put("Systems",systemsArray);

        return bgsDataOutput;

    }

    private void buildFactionData() {
        getFactionsFromSystemList();
        getFactionData();

    }

    private void getFactionData() {
        factionsMap = factionService.getFactions(factionSet);
    }

    private void getFactionsFromSystemList() {
        factionSet = FactionsFromSystemsExtractor.getFactionSetFromSystemMap(systemsMap);
    }

    private void buildSystemData () {

        systemService.buildSystemsMap(nearbySystemList);
        systemsMap = systemService.getSystemsMap();

        logger.info("Fetched " +  systemsMap.size() + " systems.");
    }

}
