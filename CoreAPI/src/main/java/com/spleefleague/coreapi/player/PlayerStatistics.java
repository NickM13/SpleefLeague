package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.database.variable.DBVariable;
import com.spleefleague.coreapi.player.statistics.Statistics;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class PlayerStatistics extends DBVariable<Document> {

    protected final Map<String, Statistics> statistics;
    protected RatedPlayer owner;

    public PlayerStatistics() {
        this.statistics = new HashMap<>();
        this.owner = null;
    }

    public void setOwner(RatedPlayer owner) {
        this.owner = owner;
    }

    @Override
    public void load(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            statistics.put(entry.getKey(), new Statistics((Document) entry.getValue()));
        }
    }

    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Statistics> statsEntry : statistics.entrySet()) {
            doc.append(statsEntry.getKey(), statsEntry.getValue().save());
        }
        return doc;
    }

    /**
     * Get the ranking stats by name
     *
     * @param name Name of Stats Group
     * @return Stats Group
     */
    public Statistics get(String name) {
        if (!statistics.containsKey(name)) {
            statistics.put(name, new Statistics());
        }
        return statistics.get(name);
    }

}
