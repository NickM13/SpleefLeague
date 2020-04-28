/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.game;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.util.CoreUtils;
import com.spleefleague.core.util.variable.Day;
import com.spleefleague.core.database.variable.DBEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

/**
 * All leaderboards are stored in the Core database Leaderboard collection
 *
 * Leaderboards contain a set of players in order defined
 * by their points
 *
 * @author NickM13
 */
public abstract class Leaderboard extends DBEntity {
    
    static class RankedPlayer {
        public CorePlayer cp;
        public int score;
    }
    
    static class RankedPlayerComparator implements Comparator<UUID> {
        String name;
        
        public RankedPlayerComparator(String name) {
            this.name = name;
        }
        
        @Override
        public int compare(UUID p1, UUID p2) {
            if (p1.equals(p2)) return 0;
            int score1 = Core.getInstance().getPlayers().get(p1).getRatings().get(name);
            int score2 = Core.getInstance().getPlayers().get(p2).getScore(name);
            if (score1 < score2) {
                return -1;
            }
            return 1;
        }
    }
    
    @DBField
    protected String name;
    // Day to reset leaderboard
    @DBField
    protected Integer createDay;
    @DBField
    protected List<UUID> players;
    
    protected String displayName;
    protected ItemStack displayItem;
    protected String description;
    
    public Leaderboard(String name, String displayName, ItemStack displayItem, String description) {
        this.name = name;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.description = description;
        reset();
    }
    
    public String getDisplayName() {
        return displayName;
    }
    public ItemStack getDisplayItem() {
        return displayItem;
    }
    public String getDescription() {
        return description;
    }
    
    public void reset() {
        players = new ArrayList<>();
        createDay = Day.getCurrentDay();
    }
    
    public abstract void checkResetDay();
    
    public List<UUID> getPlayers() {
        return Lists.newArrayList(players);
    }
    
    public String getName() {
        return name;
    }
    
    public UUID first() {
        if (players.isEmpty()) return null;
        return players.get(0);
    }
    
    public UUID getAt(int id) {
        if (id >= players.size()) return null;
        return players.toArray(new UUID[players.size()])[id];
    }
    
    public int getPlaceOf(UUID player) {
        return players.indexOf(player) + 1;
    }
    
    public void updatePlayer(UUID player) {
        if (players.contains(player))
            players.remove(player);
        
        CorePlayer cp1, cp2;
        cp1 = Core.getInstance().getPlayers().get(player);
        boolean inserted = false;
        for (int i = 0; i < players.size(); i++) {
            cp2 = Core.getInstance().getPlayers().getOffline(players.get(i));
            if (cp1.getScore(name) > cp2.getScore(name)) {
                players.add(i, player);
                inserted = true;
                break;
            }
        }
        if (!inserted) {
            players.add(player);
        }
    }
    
}
