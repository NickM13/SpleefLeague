package com.spleefleague.core.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/10/2021
 */
public class BattleSessionManager {

    private MongoCollection<Document> battleSessionCol;

    private final Map<String, Integer> ongoingModeMap = new HashMap<>();

    private BukkitTask refreshTask;

    public void init() {
        battleSessionCol = Core.getInstance().getPluginDB().getCollection("BattleSessions");

        refreshTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::refresh, 20L, 100L);
    }

    public void close() {

    }

    public void refresh() {
        ongoingModeMap.clear();
    }

    public int getOngoing(String mode) {
        if (!ongoingModeMap.containsKey(mode)) {
            ongoingModeMap.put(mode, (int) battleSessionCol.countDocuments(new Document("mode", mode)));
        }
        return ongoingModeMap.get(mode);
    }

}
