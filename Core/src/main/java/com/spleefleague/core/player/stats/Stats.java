package com.spleefleague.core.player.stats;

import com.spleefleague.core.database.variable.DBVariable;
import org.bson.Document;

import java.util.Map;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class Stats extends DBVariable<Document> {
    
    protected Map<String, Integer> stats;
    
    public Stats() {
    
    }
    
    public Map<String, Integer> getStats() {
        return stats;
    }
    
    public boolean hasStat(String name) {
        return stats.containsKey(name);
    }
    
    protected void attemptInitStat(String name) {
        if (stats.containsKey(name)) return;
        stats.put(name, 0);
    }
    
    public void setStat(String name, int value) {
        stats.put(name, value);
    }
    
    public void addStat(String name, int value) {
        attemptInitStat(name);
        stats.put(name, stats.get(name) + value);
    }
    
    public int getStat(String name) {
        attemptInitStat(name);
        return stats.get(name);
    }
    
    @Override
    public void load(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            stats.put(entry.getKey(), (Integer) entry.getValue());
        }
    }
    
    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Integer> stat : stats.entrySet()) {
            doc.append(stat.getKey(), stat.getValue());
        }
        return doc;
    }
}
