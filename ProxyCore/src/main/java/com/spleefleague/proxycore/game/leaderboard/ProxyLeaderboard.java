package com.spleefleague.proxycore.game.leaderboard;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import com.spleefleague.coreapi.player.statistics.Rating;
import com.spleefleague.proxycore.ProxyCore;
import com.spleefleague.proxycore.player.ProxyCorePlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class ProxyLeaderboard extends Leaderboard {

    private boolean modified = false;

    public ProxyLeaderboard() {
        super();
    }

    public ProxyLeaderboard(String name, String season) {
        super(name, season);
        modified = true;
    }

    /**
     * Inserts a player's score into the leaderboard, removing them first if they already existed
     *
     * @param player Player UUID
     * @param rating Rating
     */
    @Override
    public void setPlayerScore(UUID player, String username, Rating rating) {
        if (rating.getDivision() != Rating.Division.UNRANKED) {
            // Send message for becoming ranked?
            super.setPlayerScore(player, username, rating);
            modified = true;
        }
    }

    public void removePlayerScore(UUID player) {
        Integer prevScore = this.playerScoreMap.remove(player);
        if (prevScore != null) {
            scorePlayersMap.get(prevScore).remove(player);
            if (scorePlayersMap.get(prevScore).isEmpty()) {
                scorePlayersMap.remove(prevScore);
            }
        }
        modified = true;
    }

    @DBLoad(fieldName = "players")
    protected void loadPlayers(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            LeaderboardEntry leaderboardEntry = new LeaderboardEntry(uuid);
            leaderboardEntry.load((Document) entry.getValue());
            setPlayerScore(uuid, leaderboardEntry);
        }
    }

    @DBSave(fieldName = "players")
    protected Document savePlayers() {
        Document doc = new Document();
        for (Map.Entry<Integer, LinkedHashMap<UUID, LeaderboardEntry>> entry : scorePlayersMap.entrySet()) {
            for (LeaderboardEntry leaderboardEntry : entry.getValue().values()) {
                doc.append(leaderboardEntry.getUniqueId().toString(), leaderboardEntry.toDocument());
            }
        }
        return doc;
    }

    public boolean onSave() {
        if (modified) {
            modified = false;
            return true;
        }
        return false;
    }

}
