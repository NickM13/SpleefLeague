package com.spleefleague.proxycore.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.coreapi.database.variable.DBPlayer;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class LeaderboardManager {

    private final Map<String, LeaderboardCollection> LEADERBOARDS = new HashMap<>();

    private MongoCollection<Document> leaderboardCol;

    private ScheduledTask decayTask, saveTask;

    public void init() {
        leaderboardCol = ProxyCore.getInstance().getDatabase().getCollection("Leaderboards");
        for (Document doc : leaderboardCol.find(new Document())) {
            Leaderboard leaderboard;
            Boolean active = doc.get("active", Boolean.class);
            if (active == null) continue;
            if (active) {
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

        decayTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::checkDecay, 0, 6, TimeUnit.HOURS);
        saveTask = ProxyCore.getInstance().getProxy().getScheduler().schedule(ProxyCore.getInstance(), this::save, 15, 15, TimeUnit.SECONDS);
    }

    private boolean saving = false;

    public void save() {
        if (!saving) {
            saving = true;
            try {
                if (leaderboardCol.find(new Document("active", true)).first() != null) {
                    leaderboardCol.deleteMany(new Document("active", true));
                }
                for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
                    leaderboardCol.insertOne(entry.getValue().getActive().toDocument());
                }
            } catch (NoClassDefFoundError | IllegalAccessError ignored) {

            }
            saving = false;
        }
    }

    public void close() {
        decayTask.cancel();
        saveTask.cancel();
        save();
    }

    public LeaderboardCollection get(String name) {
        if (!LEADERBOARDS.containsKey(name)) {
            LEADERBOARDS.put(name, new LeaderboardCollection(name));
        }
        return LEADERBOARDS.get(name);
    }

    public void startNewSeason() {
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
    public void debug(ProxyCorePlayer pcp, int value) {
        //for (Map.Entry<String, LeaderboardCollection> entry : LEADERBOARDS.entrySet()) {
        //    pcp.getRatings().addElo(entry.getKey(), entry.getValue().getActive().getSeason(), value);
        //}
    }

    /**
     * Check the decay of every leaderboard's current season players
     */
    public void checkDecay() {
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
                    if (pcp.getRatings().checkDecay(mode, season)) {
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
