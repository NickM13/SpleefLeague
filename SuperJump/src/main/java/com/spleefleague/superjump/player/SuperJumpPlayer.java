/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.player;

import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.util.variable.Day;
import com.spleefleague.superjump.game.SJMode;

/**
 * @author NickM13
 */
public class SuperJumpPlayer extends DBPlayer {
    
    @DBField
    protected Integer points;
    @DBField
    protected Integer paragons;
    @DBField
    protected Integer paragonCoins;
    @DBField
    protected EndlessStats endlessStats;
    @DBField
    protected ConquestStats conquestStats;
    
    public SuperJumpPlayer() {
        super();
        points = 0;
        paragons = 0;
        paragonCoins = 0;
        endlessStats = new EndlessStats();
        conquestStats = new ConquestStats();
    }
    
    @Override
    public void init() {
        endlessStats.setPlayer(this);
        conquestStats.setPlayer(this);
    }

    @Override
    public void close() {

    }

    public int getPoints() {
        return points;
    }
    public int getParagons() {
        return paragons;
    }
    public int getParagonCoins() {
        return paragonCoins;
    }
    
    public EndlessStats getEndlessStats() {
        if (endlessStats.getDay() != Day.getCurrentDay())
            endlessStats.renew(Day.getCurrentDay());
        return endlessStats;
    }
    
    public ConquestStats getConquestStats() {
        return conquestStats;
    }
    
    @Override
    public void printStats(DBPlayer dbp) {
        dbp.getPlayer().sendMessage(Chat.DEFAULT + "[" + Chat.GAMEMODE + SJMode.ENDLESS.getArenaMode().getDisplayName() + Chat.DEFAULT + "]: " +
                Chat.ELO + endlessStats.getHighestLevel());
    }

    /**
     * Get the elo of a player in a certain mode
     * Warning: Not cross-dbplayer compatible, Elos are saved
     * in plugin specific variables and database
     *
     * @param arenaMode ArenaMode
     * @return Elo
     */
    @Override
    public int getRating(ArenaMode arenaMode) {
        return 0;
    }

    @Override
    public void addRating(ArenaMode arenaMode, int i) {

    }

    @Override
    public String getDisplayElo(ArenaMode arenaMode) {
        return null;
    }

}
