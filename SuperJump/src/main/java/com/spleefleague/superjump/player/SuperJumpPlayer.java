/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump.player;

import com.spleefleague.core.player.CoreDBPlayer;

/**
 * @author NickM13
 */
public class SuperJumpPlayer extends CoreDBPlayer {
    
    @Override
    public void init() {
    
    }
    
    @Override
    public void initOffline() {
    
    }
    
    @Override
    public void close() {
    
    }
    
    /*
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
        //conquestStats.setPlayer(this);
    }
    
    @Override
    public void initOffline() {
    
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
    */

}
