package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;

/**
 * @author NickM13
 * @since 6/14/2020
 */
public abstract class RatedPlayer extends DBPlayer {

    @DBField protected PlayerStatistics statistics;

    public RatedPlayer() {
        statistics = new PlayerStatistics();
    }

    /**
     * Called when a player comes online
     * Initialize DBPlayer with default values
     */
    @Override
    public void init() {
        statistics.setOwner(this);
    }

    @Override
    public void initOffline() {
        statistics.setOwner(this);
    }

    /**
     * Called when player goes offline
     */
    @Override
    public void close() {

    }

    /**
     * Get the stats object of a Core Player
     *
     * @return Core Player Stats
     * @see PlayerStatistics
     */
    public PlayerStatistics getStatistics() {
        return statistics;
    }

    public void updateLeaderboard(String mode, int season) {

    }

}
