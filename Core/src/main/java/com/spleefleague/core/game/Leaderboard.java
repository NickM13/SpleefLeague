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
public class Leaderboard extends DBEntity {
    
    private static final Map<String, Leaderboard> LEADERBOARDS = new TreeMap<>();
    private static MongoCollection<Document> lbCollection = null;
    
    public static void init() {
        lbCollection = Core.getInstance().getPluginDB().getCollection("Leaderboards");
        MongoCursor<Document> it = lbCollection.find().iterator();
    }
    public static void init(String name, LeaderboardStyle style, String displayName, ItemStack displayItem, String description) {
        Document doc;
        Leaderboard leaderboard = new Leaderboard(name, style, displayName, displayItem, description);
        if ((doc = (Document) lbCollection.find(new Document("name", name)).first()) != null) {
            leaderboard.load(doc);
        }
        leaderboard.checkResetDay();
        LEADERBOARDS.put(name, leaderboard);
    }
    
    public static Leaderboard getLeaderboard(String name) {
        if (LEADERBOARDS.containsKey(name))
            return LEADERBOARDS.get(name);
        return null;
    }
    public static Map<String, Leaderboard> getLeaderboards() {
        return LEADERBOARDS;
    }
    public static Set<String> getLeaderboardNames() {
        return LEADERBOARDS.keySet();
    }
    public static Set<String> getLeaderboardStyles() {
        return CoreUtils.enumToSet(LeaderboardStyle.class);
    }
    
    public static void close() {
        if (lbCollection == null) return;
        
        for (HashMap.Entry<String, Leaderboard> lb : LEADERBOARDS.entrySet()) {
            Document doc = lb.getValue().save();
            if (lbCollection.find(new Document("name", lb.getValue().getName())).first() != null) {
                lbCollection.replaceOne(new Document("name", lb.getValue().getName()), doc);
            } else {
                lbCollection.insertOne(doc);
            }
        }
    }
    public static void refreshPlayer(String name, UUID player) {
        LEADERBOARDS.get(name).updatePlayer(player);
    }
    public static UUID getLeadingPlayer(String name) {
        return LEADERBOARDS.get(name).first();
    }
    public static String getLeadingPlayerName(String name) {
        UUID player = LEADERBOARDS.get(name).first();
        return player != null ? Core.getInstance().getPlayers().getOffline(player).getDisplayName() : "No Lead";
    }
    public static int getLeadingPlayerScore(String name) {
        UUID player = LEADERBOARDS.get(name).first();
        return player != null ? Core.getInstance().getPlayers().getOffline(LEADERBOARDS.get(name).first()).getScore(name) : 0;
    }
    public static int getPlace(String name, UUID player) {
        return LEADERBOARDS.get(name).getPlaceOf(player);
    }
    public static void setPlayerScore(String name, UUID player, int score) {
        Core.getInstance().getPlayers().get(player).setScore(name, score);
        LEADERBOARDS.get(name).updatePlayer(player);
    }
    /** 
     * Only sets player score if its higher than their previous best
     * Used for SJ Endless
     * @param name
     * @param player
     * @param score
     */
    public static void checkPlayerScore(String name, UUID player, int score) {
        Core.getInstance().getPlayers().get(player).checkScore(name, score);
        LEADERBOARDS.get(name).updatePlayer(player);
    }

    /**
     * Reset style of leaderboards
     */
    public enum LeaderboardStyle {
        ALLTIME,
        YEARLY,
        BIANNUALLY,
        QUARTERLY,
        MONTHLY,
        WEEKLY,
        DAILY;
    }
    
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
            int score1 = Core.getInstance().getPlayers().get(p1).getScore(name);
            int score2 = Core.getInstance().getPlayers().get(p2).getScore(name);
            if (score1 < score2) {
                return -1;
            }
            return 1;
        }
    }
    
    @DBField
    private String name;
    // Day to reset leaderboard
    @DBField
    private Integer createDay;
    private LeaderboardStyle style;
    @DBField
    private List<UUID> players;
    
    private String displayName;
    private ItemStack displayItem;
    private String description;
    
    public Leaderboard(String name, LeaderboardStyle style, String displayName, ItemStack displayItem, String description) {
        this.name = name;
        this.style = style;
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
    
    public void checkResetDay() {
        switch (style) {
            case DAILY:
                if (createDay + 1 <= Day.getCurrentDay()) {
                    reset();
                }
                break;
            case WEEKLY:
                if (createDay + 7 <= Day.getCurrentDay()) {
                    reset();
                }
                break;
            case ALLTIME: default:
                
                break;
        }
    }
    
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
