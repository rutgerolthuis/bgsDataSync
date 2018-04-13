package nu.olthuis.BgsDataSync.Translators;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FactionsFromSystemsExtractor {

    public static Set<String> getFactionSetFromSystemMap(Map<String,JSONObject> systemsMap) {
        Set<String> factionSet = new HashSet<>();

        systemsMap.forEach((systemName,systemObject)-> {
            JSONArray factionArray = (JSONArray) systemObject.get("factions");

            Iterator<JSONObject> factionsIterator = factionArray.iterator();
            while (factionsIterator.hasNext()) {
                JSONObject factionObject = factionsIterator.next();
                //System.out.println("Faction:  " + factionObject.get("name"));

                if(!factionSet.contains(factionObject.get("name"))){
                    factionSet.add((String) factionObject.get("name"));
                }
            }

        });

        return factionSet;

    }

}
/*
//String systemName = ( String)systemObject.get("name");
            JSONArray factionArray = (JSONArray) systemObject.get("factions");
            //System.out.println("SystemName: " + systemName);
            //System.out.println("Factions:  " + factionArray);
            Iterator<JSONObject> factionsIterator = factionArray.iterator();

            while (factionsIterator.hasNext()) {
                JSONObject factionObject = factionsIterator.next();
                //System.out.println("Faction:  " + factionObject.get("name"));

                if(!factionSet.contains(factionObject.get("name"))){
                    factionSet.add((String) factionObject.get("name"));
                }
            }
 */