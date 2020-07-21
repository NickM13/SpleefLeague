package com.spleefleague.coreapi.player;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBPlayer;

/**
 * @author NickM13
 * @since 6/14/2020
 */
public abstract class RatedPlayer extends DBPlayer {

    @DBField protected PlayerStatistics statistics;
    @DBField protected PlayerRatings ratings;

    public RatedPlayer() {
        statistics = new PlayerStatistics();
        ratings = new PlayerRatings();
    }

    /**
     * Called when a player comes online
     * Initialize DBPlayer with default values
     */
    @Override
    public void init() {
        statistics.setOwner(this);
        ratings.setOwner(this);
    }

    @Override
    public void initOffline() {
        statistics.setOwner(this);
        ratings.setOwner(this);
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

    /**
     * Get all ratings object of a Core Player
     *
     * @return Core Player Ratings
     * @see PlayerRatings
     */
    public PlayerRatings getRatings() {
        return ratings;
    }

    public void updateLeaderboard(String mode, int season) {

    }

}
