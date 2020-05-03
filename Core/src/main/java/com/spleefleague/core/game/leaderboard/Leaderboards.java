package com.spleefleague.core.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import org.bson.Document;
import org.bukkit.Bukkit;
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
    
        /**
         * Decay task runs once every 6 hours (20 * 60 * 60 * 6)
         */
        decayTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), () -> {
            checkDecay();
        }, 0L, 20 * 60 * 60 * 6);
    }
    
    public static void close() {
        decayTask.cancel();
        try {
            if (leaderboardCol.find(new Document("active", true)).first() != null) {
                leaderboardCol.deleteMany(new Document("active", true));
            }
            for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
                leaderboardCol.insertOne(entry.getValue().getActive().save());
            }
        } catch (NoClassDefFoundError | IllegalAccessError ignored) {
        
        }
    }
    
    public static LeaderboardCollection get(String name) {
        if (!LEADERBOARDS.containsKey(name)) {
            LEADERBOARDS.put(name, new LeaderboardCollection(name));
        }
        return LEADERBOARDS.get(name);
    }
    
    public static void startNewSeason() {
        for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
            leaderboardCol.insertOne(entry.getValue().startNewSeason().save());
        }
    }
    
    /**
     * Test function to add player to leaderboard
     *
     * @param cp Core Player
     * @param value Rating
     */
    public static void debug(CorePlayer cp, int value) {
        for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
            cp.getRatings().addElo(entry.getKey(), entry.getValue().getActive().getSeason(), value);
        }
    }
    
    /**
     * Check the decay of every leaderboard's current season players
     */
    public static void checkDecay() {
        for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
            Map<Integer, Set<UUID>> oldPlayerScores = new HashMap<>();
            Map<Integer, Set<UUID>> newPlayerScores = new HashMap<>();
            ActiveLeaderboard activeLeaderboard = entry.getValue().getActive();
            String mode = entry.getKey();
            int season = activeLeaderboard.getSeason();
            for (UUID uuid : entry.getValue().getActive().getPlayerScoreMap().keySet()) {
                CorePlayer cp = Core.getInstance().getPlayers().getOffline(uuid);
                if (cp.getRatings().isRanked(mode, season)) {
                    int prevScore = cp.getRatings().getElo(mode, season);
                    if (cp.getRatings().checkDecay(mode, season)) {
                        oldPlayerScores.getOrDefault(prevScore,
                                oldPlayerScores.put(prevScore, new HashSet<>())).add(uuid);
                        newPlayerScores.getOrDefault(cp.getRatings().getElo(mode, season),
                                newPlayerScores.put(cp.getRatings().getElo(mode, season), new HashSet<>())).add(uuid);
                        
                        if (!cp.isOnline()) {
                            Core.getInstance().getPlayers().save(cp);
                        }
                    }
                }
            }
            activeLeaderboard.sortMany(oldPlayerScores, newPlayerScores);
        }
    }
    
}
