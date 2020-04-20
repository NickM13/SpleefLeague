/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.spleef.player;

import com.google.common.collect.Lists;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBLoad;
import com.spleefleague.core.database.annotation.DBSave;
import com.spleefleague.core.database.variable.DBPlayer;
import com.spleefleague.core.game.ArenaMode;
import com.spleefleague.core.vendor.Vendorables;
import com.spleefleague.spleef.game.Shovel;
import com.spleefleague.spleef.game.spleef.power.Power;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

/**
 * @author NickM13
 */
public class SpleefPlayer extends DBPlayer {
    
    private static final Integer BASE_RATING = 1000;
    
    //@DBField
    protected Integer[] activePowers = new Integer[4];
    
    protected Map<ArenaMode, Integer> ratings = new HashMap<>();
    
    public SpleefPlayer() {
        super();
        for (int i = 0; i < 4; i++) {
            activePowers[i] = Power.getDefaultPower(i).getDamage();
        }
    }

    @Override
    public void init() { }

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
    
    @DBLoad(fieldName="ratings")
    private void loadRatings(List<Object> ratings) {
        if (ratings == null) return;
        Document d;
        for (Object o : ratings) {
            d = (Document) o;
            this.ratings.put(ArenaMode.getArenaMode(d.get("mode", String.class)), d.get("rating", Integer.class));
        }
    }
    @DBSave(fieldName="ratings")
    private List<Document> saveRatings() {
        if (ratings.isEmpty()) return null;
        
        List<Document> list = new ArrayList<>();
        
        for (Map.Entry<ArenaMode, Integer> rating : ratings.entrySet()) {
            list.add(new Document("mode", rating.getKey().getName()).append("rating", rating.getValue()));
        }
        
        return list;
    }
    public String getDisplayElo(ArenaMode mode) {
        return (Chat.BRACE + "(" +
                Chat.ELO + getRating(mode) +
                Chat.BRACE + ")");
    }
    public int getRating(ArenaMode mode) {
        if (!ratings.containsKey(mode)) ratings.put(mode, BASE_RATING);
        return ratings.get(mode);
    }
    public void setRating(ArenaMode mode, int amt) {
        ratings.put(mode, amt);
    }
    public void addRating(ArenaMode mode, int amt) {
        Core.getInstance().sendMessage(getPlayer(), "You have " +
                (amt > 0 ? "gained " : "lost ") +
                Chat.ELO + Math.abs(amt) +
                Chat.DEFAULT + " elo");
        ratings.put(mode, getRating(mode) + amt);
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

    @Override
    @Deprecated
    public void printStats(DBPlayer dbp) {
        for (Map.Entry<ArenaMode, Integer> rating : ratings.entrySet()) {
            dbp.getPlayer().sendMessage(Chat.DEFAULT + "[" + Chat.GAMEMODE + rating.getKey().getDisplayName() + Chat.DEFAULT + "]: " +
                    Chat.ELO + rating.getValue());
        }
    }
    
}
