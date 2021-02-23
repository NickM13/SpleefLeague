package com.spleefleague.core.game.season;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.settings.Settings;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/20/2021
 */
public class SeasonManager {

    public static class Season extends DBEntity {

        @DBField private String name;
        @DBField private Boolean preseason;

        public Season() {

        }

        public String getDisplayName() {
            return name;
        }

        public boolean isPreseason() {
            return preseason;
        }

    }

    private static MongoCollection<Document> seasonColl;

    private static final Map<String, Season> SEASON = new HashMap<>();

    private static Season currentSeason;

    public static void init() {
        seasonColl = Core.getInstance().getPluginDB().getCollection("Seasons");
        for (Document doc : seasonColl.find()) {
            Season season = new Season();
            season.load(doc);
            SEASON.put(season.getIdentifier(), season);
        }
        currentSeason = getSeasonInfo(Settings.getCurrentSeason());
    }

    public static void close() {

    }

    public static Season getCurrentSeason() {
        return currentSeason;
    }

    public static Season getSeasonInfo(String identifier) {
        return SEASON.get(identifier);
    }

}
