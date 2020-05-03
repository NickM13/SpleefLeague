package com.spleefleague.core.player;

import com.spleefleague.core.database.variable.DBVariable;
import com.spleefleague.core.game.BattleMode;
import com.spleefleague.core.player.stats.Stats;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class CorePlayerStats extends DBVariable<Document> {
    
    protected final Map<String, Stats> stats;
    protected CorePlayer owner;
    
    public CorePlayerStats() {
        this.stats = new HashMap<>();
        this.owner = null;
    }
    
    public void setOwner(CorePlayer owner) {
        this.owner = owner;
    }
    
    @Override
    public void load(Document doc) {
    
    }
    
    @Override
    public Document save() {
        Document doc = new Document();
        for (Map.Entry<String, Stats> statsEntry : stats.entrySet()) {
            doc.append(statsEntry.getKey(), statsEntry.getValue().save());
        }
        return doc;
    }
    
    /*
    public Rating getRating(String mode) {
        if (!ratings.containsKey(mode)) {
            ratings.put(mode, new Rating());
        }
        return ratings.get(mode);
    }
     */
    
    /**
     * Get the ranking stats by the name of a mode
     *
     * @param mode Arena Mode (not hard set)
     * @return Elo
     */
    public Stats getStats(BattleMode mode) {
        if (!stats.containsKey(mode.getName())) {
            stats.put(mode.getName(), new Stats());
        }
        return stats.get(mode.getName());
    }
    
    /**
     * Get the ranking stats by name
     *
     * @param name Name of Stats Group
     * @return Stats Group
     */
    public Stats getStats(String name) {
        if (!stats.containsKey(name)) {
            stats.put(name, new Stats());
        }
        return stats.get(name);
    }
    
    /**
     * Get a formatted String of the elo of a player in a certain mode
     *
     * @param mode Arena Mode (not hard set)
     * @return Elo as a formatted String
     */
    public String getDisplayElo(BattleMode mode) {
        return "";
        /*
        return (Chat.BRACKET + "("
                + ratings.get(mode.getName()).getDisplayElo()
                + Chat.BRACKET + ")");
         */
    }
    
}
