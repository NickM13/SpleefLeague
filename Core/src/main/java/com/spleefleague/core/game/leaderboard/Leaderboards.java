package com.spleefleague.core.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.coreapi.game.leaderboard.ActiveLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.ArchivedLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import org.bson.Document;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class Leaderboards {
    
    private static final Map<String, LeaderboardCollection> LEADERBOARDS = new HashMap<>();
    
    private static MongoCollection<Document> leaderboardCol;
    
    private static BukkitTask decayTask;
    
    public static void init() {
        leaderboardCol = Core.getInstance().getPluginDB().getCollection("Leaderboards");
        for (Document doc : leaderboardCol.find(new Document())) {
            Leaderboard leaderboard;
            if (doc.get("active", Boolean.class)) {
                leaderboard = new ActiveLeaderboard();
            } else {
                leaderboard = new ArchivedLeaderboard();
            }
            leaderboard.load(doc);
            if (!LEADERBOARDS.containsKey(leaderboard.getName())) {
                LEADERBOARDS.put(leaderboard.getName(), new LeaderboardCollection(leaderboard.getName()));
            }
            LEADERBOARDS.get(leaderboard.getName()).addLeaderboard(leaderboard);
        }
    }
    
    public static void close() {

    }
    
    public static LeaderboardCollection get(String name) {
        if (!LEADERBOARDS.containsKey(name)) {
            LEADERBOARDS.put(name, new LeaderboardCollection(name));
        }
        return LEADERBOARDS.get(name);
    }
    
}
