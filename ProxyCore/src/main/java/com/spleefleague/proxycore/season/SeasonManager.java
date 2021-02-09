package com.spleefleague.proxycore.season;

import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.spleefleague.proxycore.ProxyCore;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author NickM13
 * @since 2/7/2021
 */
public class SeasonManager {

    private MongoCollection<Document> seasonCollection;
    private MongoCollection<Document> settingsCollection;

    private Map<Integer, SeasonInfo> seasonInfoMap = new HashMap<>();

    private int currentSeason;

    public SeasonManager() {
        currentSeason = 0;
        seasonInfoMap.put(currentSeason, new SeasonInfo());
    }

    public void init() {
        seasonCollection = ProxyCore.getInstance().getDatabase().getCollection("Seasons");
        settingsCollection = ProxyCore.getInstance().getDatabase().getCollection("Settings");

        for (Document doc : seasonCollection.find()) {
            SeasonInfo seasonInfo = new SeasonInfo();
            seasonInfo.load(doc);
            seasonInfoMap.put(seasonInfo.getSeasonId(), seasonInfo);
        }

        currentSeason = Objects.requireNonNull(settingsCollection.find(new Document("identifier", "season")).first()).getInteger("current");
    }

    public void close() {

    }

    public int getCurrentSeason() {
        return currentSeason;
    }

}
