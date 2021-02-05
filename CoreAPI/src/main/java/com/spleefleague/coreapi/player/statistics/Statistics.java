package com.spleefleague.coreapi.player.statistics;

import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class Statistics extends DBVariable<Document> {

    protected Map<String, Long> statistics = new HashMap<>();

    public Statistics() { }

    public Statistics(Document doc) {
        load(doc);
    }

    public Map<String, Long> getStatistics() {
        return statistics;
    }

    public boolean has(String name) {
        return statistics.containsKey(name);
    }

    public void set(String name, long value) {
        statistics.put(name, value);
    }

    public long add(String name, long value) {
        if (!statistics.containsKey(name)) {
            statistics.put(name, value);
        } else {
            statistics.put(name, statistics.get(name) + value);
        }
        return statistics.get(name);
    }

    /**
     * Sets a new stat if value is higher than previous statistic, returning the change
     *
     * @param name Name
     * @param value Value
     * @return Change
     */
    public long setHigher(String name, long value) {
        if (!statistics.containsKey(name)) {
            statistics.put(name, value);
            return value;
        } else if (value > statistics.get(name)) {
            long prev = statistics.get(name);
            statistics.put(name, value);
            return value - prev;
        }
        return 0;
    }

    /**
     * Sets a new stat if value is higher than previous statistic, returning the change
     *
     * @param name Stat Name
     * @param compare Compared Stat Name
     * @return Change
     */
    public long setHigher(String name, String compare) {
        return setHigher(name, get(compare));
    }

    public long get(String name) {
        return statistics.getOrDefault(name, 0L);
    }

    @Override
    public void load(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            statistics.put(entry.getKey(), (Long) entry.getValue());
        }
    }

    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Long> stat : statistics.entrySet()) {
            doc.append(stat.getKey(), stat.getValue());
        }
        return doc;
    }
}
