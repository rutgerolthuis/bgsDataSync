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

        fillNearbySystems(range);
        logger.info("Fill Nearby Systems done");
        buildSystemData();
        logger.info("Build system data done");
        buildFactionData();
        logger.info("Faction data build");
        insertFactionDataInSystems();
        logger.info("Factions inserted into systems");

        return mergeSystems();

    }

    private void fillNearbySystems(int referenceDistance) {

        nearbySystemList = habitatSystemsService.getNearbySystems(referenceDistance);
        System.out.println("Found " + nearbySystemList.size() + " systems within " + referenceDistance + " ly of Kolaga. ");
    }

    @SuppressWarnings("unchecked")
    private void insertFactionDataInSystems() {

        factionsMap.forEach((factionName,factionObject) -> {

            JSONArray factionPresence = (JSONArray) factionObject.get("faction_presence");
            JSONArray factionHistory = (JSONArray) factionObject.get("history");
            Iterator<?> factionPresenceIterator = factionPresence.iterator();

            factionPresenceIterator.forEachRemaining(localFactionPresence -> {

                String systemNameForLocalFaction = (String) ((JSONObject) localFactionPresence).get("system_name");
                logger.info("Trying to add the faction info for system: " + systemNameForLocalFaction +  " and faction: " + factionName );

                JSONObject systemObject = systemsMap.get(systemNameForLocalFaction);
                if (systemObject == null) {
                    // very well possible that the faction is in a system we do not watch...
                    logger.info("No system object found for system: " +  systemNameForLocalFaction + "; skipping.");
                    return;
                }

                JSONArray factionsFromSystem = (JSONArray) systemObject.get("factions");
                Iterator<?> factionsFromSystemIterator = factionsFromSystem.iterator();

                factionsFromSystemIterator.forEachRemaining(systemFaction -> {

                    if (((JSONObject) systemFaction).get("name").toString().equals(factionName)) {

                        logger.info("Writing localFactionPresence and history for faction : " + factionName + " in system " + systemNameForLocalFaction);
                        //System.out.println(systemFaction);
                        //System.out.println(localFactionPresence);
                        //System.out.println(factionHistory);

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
        logger.info("Fetched all the factions from the systemlist: " + factionSet.size());
        getFactionData();
        logger.info("Fetched all the faction data : " + factionsMap.size());

    }

    private void getFactionData() {
        factionsMap = factionService.getFactions(factionSet);
    }

    private void getFactionsFromSystemList() {
        factionSet = FactionsFromSystemsExtractor.getFactionSetFromSystemMap(systemsMap);
    }

    private void buildSystemData () {

        systemService.buildSystemsMap(nearbySystemList);
        System.out.println("System data build");
        systemsMap = systemService.getSystemsMap();
        System.out.println("Fetched " +  systemsMap.size() + " systems with data");
    }

}
