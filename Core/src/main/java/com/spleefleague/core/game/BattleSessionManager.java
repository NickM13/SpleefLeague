package com.spleefleague.core.game;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author NickM13
 * @since 2/10/2021
 */
public class BattleSessionManager {

    private MongoCollection<Document> battleSessionColl;

    private final Map<String, Integer> ongoingModeMap = new HashMap<>();

    private BukkitTask refreshTask;

    public void init() {
        battleSessionColl = Core.getInstance().getPluginDB().getCollection("BattleSessions");

        refreshTask = Bukkit.getScheduler().runTaskTimer(Core.getInstance(), this::refresh, 20L, 100L);
    }

    public void close() {
        refreshTask.cancel();
    }

    public void refresh() {
        ongoingModeMap.clear();
    }

    public int getOngoing(String... modes) {
        int total = 0;
        for (String mode : modes) {
            if (!ongoingModeMap.containsKey(mode)) {
                int count = 0;
                for (Document doc : battleSessionColl.find(new Document("mode", mode))) {
                    count += doc.get("players", List.class).size();
                }
                ongoingModeMap.put(mode, count);
            }
            total += ongoingModeMap.get(mode);
        }
        return total;
    }

}
