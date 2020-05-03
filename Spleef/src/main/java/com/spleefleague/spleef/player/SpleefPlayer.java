/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.google.common.collect.Lists;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.spleef.game.battle.power.Power;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickM13
 */
public class SpleefPlayer extends DBPlayer {
    
    private static final Integer BASE_RATING = 1000;
    
    //@DBField
    protected Integer[] activePowers = new Integer[4];
    
    public SpleefPlayer() {
        super();
        for (int i = 0; i < 4; i++) {
            activePowers[i] = Power.getDefaultPower(i).getDamage();
        }
    }

    @Override
    public void init() {
    
    }
    
    @Override
    public void initOffline() {
    
    }
    
    @Override
    public void close() { }
    
    @DBLoad(fieldName="activePowers")
    private void loadActivePowers(List<Integer> powers) {
        if (powers == null) return;
        for (int i = 0; i < Math.min(4, powers.size()); i++) {
            activePowers[i] = powers.get(i);
        }
    }
    
    @DBSave(fieldName="activePowers")
    private List<Integer> saveActivePowers() {
        return Lists.newArrayList(activePowers);
    }
    
    public void setActivePower(int slot, int powerId) {
        activePowers[slot] = powerId;
    }
    
    public Power getActivePower(int slot) {
        return Power.getPower(slot, activePowers[slot]);
    }
    
    public List<Power> getActivePowers() {
        List<Power> powers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            powers.add(Power.getPower(i, activePowers[i]));
        }
        return powers;
    }
    
}
