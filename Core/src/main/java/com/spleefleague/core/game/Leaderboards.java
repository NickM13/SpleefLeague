package com.spleefleague.core.game;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class Leaderboards {
    
    private static final Map<String, Leaderboard> LEADERBOARDS = new TreeMap<>();
    private static MongoCollection<Document> lbCollection = null;
    
    public static void init() {
        lbCollection = Core.getInstance().getPluginDB().getCollection("Leaderboards");
        MongoCursor<Document> it = lbCollection.find().iterator();
    }
    public static void init(Class<? extends Leaderboard> clazz, String name, String displayName, ItemStack displayItem, String description) {
        Document doc;
        Leaderboard leaderboard;
        try {
            leaderboard = clazz.getDeclaredConstructor(
                    String.class,
                    String.class,
                    ItemStack.class,
                    String.class)
                    .newInstance(
                            name,
                            displayName,
                            displayItem,
                            description);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        if ((doc = lbCollection.find(new Document("name", name)).first()) != null) {
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
        return 0;
        //return player != null ? Core.getInstance().getPlayers().getOffline(LEADERBOARDS.get(name).first()).getScore(name) : 0;
    }
    public static int getPlace(String name, UUID player) {
        return LEADERBOARDS.get(name).getPlaceOf(player);
    }
    public static void setPlayerScore(String name, UUID player, int score) {
        //Core.getInstance().getPlayers().get(player).setScore(name, score);
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
        //Core.getInstance().getPlayers().get(player).checkScore(name, score);
        LEADERBOARDS.get(name).updatePlayer(player);
    }
    
}
