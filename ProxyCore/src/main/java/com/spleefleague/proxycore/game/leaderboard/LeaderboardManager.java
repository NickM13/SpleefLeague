package com.spleefleague.proxycore.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import com.spleefleague.proxycore.player.ProxyPlayerRatings;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class LeaderboardManager {

    private static final Map<String, LeaderboardCollection> LEADERBOARDS = new HashMap<>();

    private static MongoCollection<Document> leaderboardCol;

    private static ScheduledTask decayTask;

    public static void init() {
        leaderboardCol = ProxyCore.getInstance().getDatabase().getCollection("Leaderboards");
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

        decayTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), LeaderboardManager::checkDecay, 0, 6, TimeUnit.HOURS);
    }

    public static void close() {
        decayTask.cancel();
        try {
            if (leaderboardCol.find(new Document("active", true)).first() != null) {
                leaderboardCol.deleteMany(new Document("active", true));
            }
            for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
                leaderboardCol.insertOne(entry.getValue().getActive().toDocument());
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
            leaderboardCol.insertOne(entry.getValue().startNewSeason().toDocument());
        }
    }

    /**
     * Test function to add player to leaderboard
     *
     * @param pcp Proxy Core Player
     * @param value Rating
     */
    public static void debug(ProxyCorePlayer pcp, int value) {
        //for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
        //    pcp.getRatings().addElo(entry.getKey(), entry.getValue().getActive().getSeason(), value);
        //}
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
                ProxyCorePlayer pcp = ProxyCore.getInstance().getPlayers().getOffline(uuid);
                if (pcp != null && pcp.getRatings().isRanked(mode, season)) {
                    int prevScore = pcp.getRatings().getElo(mode, season);
                    if (((ProxyPlayerRatings) pcp.getRatings()).checkDecay(mode, season)) {
                        oldPlayerScores.getOrDefault(prevScore,
                                oldPlayerScores.put(prevScore, new HashSet<>())).add(uuid);
                        newPlayerScores.getOrDefault(pcp.getRatings().getElo(mode, season),
                                newPlayerScores.put(pcp.getRatings().getElo(mode, season), new HashSet<>())).add(uuid);

                        if (pcp.getOnlineState() != DBPlayer.OnlineState.HERE) {
                            ProxyCore.getInstance().getPlayers().save(pcp);
                        }
                    }
                }
            }
            activeLeaderboard.sortMany(oldPlayerScores, newPlayerScores);
        }
    }

}
