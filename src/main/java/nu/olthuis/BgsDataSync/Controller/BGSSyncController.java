package nu.olthuis.BgsDataSync.Controller;

import nu.olthuis.BgsDataSync.Service.BGSSyncService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BGSSyncController {

    private final BGSSyncService bgsSyncService;

    @Autowired
    public BGSSyncController (BGSSyncService bgsSyncService) {
        this.bgsSyncService = bgsSyncService;

    }

    @RequestMapping("/BGSSync")
    public JSONObject bgsSync(@RequestParam(value="referenceSystem", defaultValue="Kolaga") String referenceSystem, @RequestParam(value="range", defaultValue="10") int range) {

        JSONObject woei = bgsSyncService.getBgsData(referenceSystem,range);
        return woei;

    }

}
