package com.spleefleague.coreapi.player.statistics;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.util.*;

/**
 * Collection of Ratings for all seasons for a game type
 *
 * @author NickM13
 * @since 4/30/2020
 */
public class Ratings extends DBEntity {

    @DBField private String name;
    private final SortedMap<String, Rating> seasonalRatings = new TreeMap<>();

    public Ratings() {

    }

    public Ratings(String name) {
        this.name = name;
    }

    @DBSave(fieldName="ratings")
    protected List<Document> saveRatings() {
        List<Document> docs = new ArrayList<>();
        for (Map.Entry<String, Rating> entry : seasonalRatings.entrySet()) {
            docs.add(new Document("season", entry.getKey()).append("rating", entry.getValue().toDocument()));
        }
        return docs;
    }

    @DBLoad(fieldName="ratings")
    protected void loadRatings(List<Document> docs) {
        for (Document doc : docs) {
            Rating rating = new Rating();
            rating.load(doc.get("rating", Document.class));
            seasonalRatings.put(doc.getString("season"), rating);
        }
    }

    /**
     * Get rating from a specified season
     *
     * @param season Season
     * @return Rating
     */
    public Rating get(String season) {
        if (!seasonalRatings.containsKey(season)) {
            seasonalRatings.put(season, new Rating());
        }
        return seasonalRatings.get(season);
    }

    public boolean isRanked(String season) {
        return seasonalRatings.containsKey(season);
    }

}
