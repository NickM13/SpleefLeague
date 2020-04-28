package com.spleefleague.core.player;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.variable.DBVariable;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.player.stats.Rating;
import com.spleefleague.core.player.stats.Stats;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/25/2020
 */
public class CorePlayerStats extends DBVariable<Document> {
    
    protected Map<String, Rating> ratings = new HashMap<>();
    protected Map<String, Stats> stats = new HashMap<>();
    protected CorePlayer owner = null;
    
    public CorePlayerStats() {
    
    }
    
    public void setOwner(CorePlayer owner) {
        this.owner = owner;
    }
    
    @Override
    public void load(Document doc) {
    
    }
    
    @Override
    public Document save() {
        return null;
    }
    
    /**
     * Get the ranking stats in a certain mode
     *
     * @param mode Arena Mode (not hard set)
     * @return Elo
     */
    public Stats get(ArenaMode mode) {
        if (!stats.containsKey(mode.getName()))
            stats.put(mode.getName(), new Stats());
        return stats.get(mode.getName());
    }
    
    /**
     * Set the elo of a player in a certain mode
     *
     * @param mode Arena Mode (not hard set)
     * @param amt Elo
     */
    public void set(String mode, int amt) {
        //ratings.put(mode, amt);
    }
    
    /**
     * Get a formatted String of the elo of a player in a certain mode
     *
     * @param mode Arena Mode (not hard set)
     * @return Elo as a formatted String
     */
    public String getDisplayElo(ArenaMode mode) {
        return (Chat.BRACKET + "("
                + Chat.ELO + get(mode)
                + Chat.BRACKET + ")");
    }
    
}
