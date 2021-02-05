package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.database.annotation.DBField;
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

    @DBField protected Map<String, Statistics> statisticsMap = new HashMap<>();

    public PlayerStatistics() {

    }

    @Override
    public void load(Document doc) {
        statisticsMap.clear();
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            Statistics statistics = new Statistics();
            statistics.load((Document) entry.getValue());
            statisticsMap.put(entry.getKey(), statistics);
        }
    }

    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Statistics> entry : statisticsMap.entrySet()) {
            doc.put(entry.getKey(), entry.getValue().save());
        }
        return doc;
    }

    public boolean has(String parent, String statName) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
            return false;
        }
        return statisticsMap.get(parent).has(statName);
    }

    public long add(String parent, String statName, long value) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
        }
        return statisticsMap.get(parent).add(statName, value);
    }

    public long get(String parent, String statName) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
        }
        return statisticsMap.get(parent).get(statName);
    }

    public void set(String parent, String statName, long value) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
        }
        statisticsMap.get(parent).set(statName, value);
    }

    public long setHigher(String parent, String statName, long value) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
        }
        return statisticsMap.get(parent).setHigher(statName, value);
    }

    public long setHigher(String parent, String statName, String compare) {
        if (!statisticsMap.containsKey(parent)) {
            statisticsMap.put(parent, new Statistics());
        }
        return statisticsMap.get(parent).setHigher(statName, compare);
    }

    /*
    public Statistics get(String name) {
        if (!statistics.containsKey(name)) {
            statistics.put(name, new Statistics());
        }
        return statistics.get(name);
    }
    */

}
