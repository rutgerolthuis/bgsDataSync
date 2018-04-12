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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class FactionService {
    private final static Logger logger = LogManager.getLogger(FactionService.class);
    private final EliteBgsConsumer eliteBgsConsumer;

    private final static String factionsUri = "https://elitebgs.kodeblox.com/api/ebgs/v4/factions" ;
    private final static String eddbFactionUri = "https://elitebgs.kodeblox.com/api/eddb/v3/factions";

    @Autowired
    public FactionService(EliteBgsConsumer eliteBgsConsumer) {
        this.eliteBgsConsumer = eliteBgsConsumer;
    }

    @SuppressWarnings("unchecked")
    public Map<String,JSONObject> getFactions(Set<String> factionSet) {
        Map<String,JSONObject> factionsMap = new HashMap<>();
        factionSet.forEach(factionName -> {

            logger.debug("Getting data for faction: " + factionName);
            Boolean isPlayerFaction = getIsPlayerFaction( factionName);
            JSONObject factionObject = getFactionObject(factionName );
            factionObject.put("is_player_faction",isPlayerFaction);
            factionsMap.put(factionName,factionObject);

        });

        return factionsMap;
    }

    private JSONObject getFactionObject(String factionName) {

        String urlString = "";
        try {
            urlString = factionsUri + "?name=" + URLEncoder.encode(factionName, "UTF-8") + "&timemax=1522800000000" ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(urlString).build(true);

        URI uri = uriComponents.toUri();

        JSONObject factionResponseObject = eliteBgsConsumer.getData(uri);
        JSONArray factionDocsObject = (JSONArray) factionResponseObject.get("docs");

        return (JSONObject) factionDocsObject.iterator().next();
    }

    private Boolean getIsPlayerFaction(String factionName) {

        String urlString = "";
        try {
            urlString = eddbFactionUri + "?name=" + URLEncoder.encode(factionName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        UriComponents uriComponents = UriComponentsBuilder.fromUriString(urlString).build(true);

        URI uri = uriComponents.toUri();

        JSONObject eddbFactionObject = eliteBgsConsumer.getData(uri );
        JSONArray eddbFactionDocsObject = (JSONArray) eddbFactionObject.get("docs");
        return (Boolean) ((JSONObject) eddbFactionDocsObject.iterator().next()).get("is_player_faction");
    }

}
