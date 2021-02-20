package com.spleefleague.core.player;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.rank.CoreRank;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author NickM13
 * @since 2/18/2021
 */
public class PlayerDisplayManager {

    public static class PlayerDisplay extends DBEntity {

        @DBField String displayNameRanked;
        @DBField String displayName;

        public PlayerDisplay(String username) {
            this.displayNameRanked = ChatColor.YELLOW + username;
            this.displayName = ChatColor.YELLOW + username;
        }

        public PlayerDisplay(Document doc) {
            load(doc);
        }

    }

    private MongoCollection<Document> playerColl;

    private final Map<UUID, PlayerDisplay> displayMap = new HashMap<>();

    public void init() {
        playerColl = Core.getInstance().getPluginDB().getCollection("PlayerDisplays");
    }

    public void close() {

    }

    public PlayerDisplay get(UUID uuid) {
        if (displayMap.containsKey(uuid)) {
            return displayMap.get(uuid);
        }
        Document doc = playerColl.find(new Document("identifier", uuid.toString())).first();
        PlayerDisplay playerDisplay;
        if (doc == null) {
            playerDisplay = new PlayerDisplay(Bukkit.getOfflinePlayer(uuid).getName());
            displayMap.put(uuid, playerDisplay);
            //playerDisplay.save(playerColl);
        } else {
            playerDisplay = new PlayerDisplay(doc);
            displayMap.put(uuid, playerDisplay);
        }
        return playerDisplay;
    }

    public void updateDisplay(UUID uuid) {
        Document doc = playerColl.find(new Document("identifier", uuid.toString())).first();
        PlayerDisplay playerDisplay;
        if (doc != null) {
            playerDisplay = new PlayerDisplay(doc);
            displayMap.put(uuid, playerDisplay);
        }
    }

}
