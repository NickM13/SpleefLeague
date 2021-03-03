package com.spleefleague.core.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.game.season.SeasonManager;
import com.spleefleague.core.settings.Settings;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class LeaderboardManager {

    private final Map<String, CoreLeaderboard> LEADERBOARDS = new HashMap<>();

    private MongoCollection<Document> leaderboardCol;

    private BukkitTask decayTask;

    public void init() {
        SeasonManager.init();
        leaderboardCol = Core.getInstance().getPluginDB().getCollection("Leaderboards");
        for (Document doc : leaderboardCol.find(new Document("season", SeasonManager.getCurrentSeason().getIdentifier()))) {
            CoreLeaderboard leaderboard = new CoreLeaderboard();
            leaderboard.load(doc);
            LEADERBOARDS.put(leaderboard.getName(), leaderboard);
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(Core.getInstance(), this::refresh, 200L, 200L);
    }

    public void clear() {
        for (CoreLeaderboard leaderboard : LEADERBOARDS.values()) {
            if (leaderboard.getSeason().equals(SeasonManager.getCurrentSeason().getIdentifier())) {
                leaderboard.clear();
            }
        }
    }

    public void refresh() {
        for (Document doc : leaderboardCol.find(new Document("season", SeasonManager.getCurrentSeason().getIdentifier()))) {
            String name = doc.getString("name");
            if (LEADERBOARDS.containsKey(name)) {
                LEADERBOARDS.get(name).loadPlayers(doc.get("players", Document.class));
            } else {
                CoreLeaderboard leaderboard = new CoreLeaderboard();
                leaderboard.load(doc);
                LEADERBOARDS.put(leaderboard.getName(), leaderboard);
            }
        }
    }

    public void close() {

    }

    public CoreLeaderboard get(String name) {
        if (!LEADERBOARDS.containsKey(name)) {
            LEADERBOARDS.put(name, new CoreLeaderboard(name, Settings.getCurrentSeason()));
        }
        return LEADERBOARDS.get(name);
    }

}
