package com.spleefleague.core.game.leaderboard;

import com.spleefleague.core.database.annotation.DBSave;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class ActiveLeaderboard extends Leaderboard {
    
    public ActiveLeaderboard() {
        super();
        active = true;
    }
    
    public ActiveLeaderboard(String name, int season) {
        super(name, season);
        active = true;
        createTime = System.currentTimeMillis();
    }
    
    /**
     * @return Leaderboard Identifier Name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return "Start Date: " + dateFormat.format(new Date(createTime));
    }
    
    /**
     * Returns all player scores, used for cloning leaderboard into an archive
     *
     * @return Map of Player UUIDs and Scores
     */
    public Map<UUID, Integer> getPlayerScoreMap() {
        return playerScoreMap;
    }
    
    /**
     * Removes a player from the leaderboard
     *
     * @param player Player UUID
     */
    public void removePlayer(UUID player) {
        Integer score = playerScoreMap.get(player);
        if (score != null) {
            scorePlayersMap.get(score).remove(player);
            if (scorePlayersMap.get(score).isEmpty()) {
                scorePlayersMap.remove(score);
            }
            playerScoreMap.remove(player);
        }
    }
    
}
