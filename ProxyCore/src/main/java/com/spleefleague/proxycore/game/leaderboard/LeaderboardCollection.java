package com.spleefleague.proxycore.game.leaderboard;

import net.md_5.bungee.api.ChatColor;

import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class LeaderboardCollection {

    protected String name;
    protected ActiveLeaderboard activeLeaderboard;
    protected final Map<Integer, Leaderboard> leaderboards = new TreeMap<>();

    public LeaderboardCollection(String name) {
        this.name = name;
        activeLeaderboard = new ActiveLeaderboard(name, 0);
        leaderboards.put(0, activeLeaderboard);
    }

    public ArchivedLeaderboard startNewSeason() {
        ArchivedLeaderboard archivedLeaderboard = new ArchivedLeaderboard(activeLeaderboard);
        leaderboards.put(activeLeaderboard.getSeason(), archivedLeaderboard);
        activeLeaderboard = new ActiveLeaderboard(name, leaderboards.size());
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

    public Map<Integer, Leaderboard> getLeaderboards() {
        return leaderboards;
    }

}
