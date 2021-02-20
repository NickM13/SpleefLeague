package com.spleefleague.coreapi.game.leaderboard;

import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class LeaderboardCollection {

    protected String name;
    protected ActiveLeaderboard activeLeaderboard;
    protected final Map<String, Leaderboard> leaderboards = new TreeMap<>();

    public LeaderboardCollection(String name, String season) {
        this.name = name;
        activeLeaderboard = new ActiveLeaderboard(name, season);
        leaderboards.put(season, activeLeaderboard);
    }

    public ArchivedLeaderboard startNewSeason(String season) {
        ArchivedLeaderboard archivedLeaderboard = new ArchivedLeaderboard(activeLeaderboard);
        leaderboards.put(activeLeaderboard.getSeason(), archivedLeaderboard);
        activeLeaderboard = new ActiveLeaderboard(name, season);
        leaderboards.put(activeLeaderboard.getSeason(), activeLeaderboard);
        return archivedLeaderboard;
    }

    public String getName() {
        return name;
    }

    public void addLeaderboard(Leaderboard leaderboard) {
        if (leaderboard.isActive()) {
            activeLeaderboard = (ActiveLeaderboard) leaderboard;
        }
        leaderboards.put(leaderboard.getSeason(), leaderboard);
    }

    public ActiveLeaderboard getActive() {
        return activeLeaderboard;
    }

    public Map<String, Leaderboard> getLeaderboards() {
        return leaderboards;
    }

}
