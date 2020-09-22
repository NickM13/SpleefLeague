package com.spleefleague.core.game.leaderboard;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.coreapi.game.leaderboard.ActiveLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.ArchivedLeaderboard;
import com.spleefleague.coreapi.game.leaderboard.Leaderboard;
import com.spleefleague.coreapi.player.PlayerRatings;
import com.spleefleague.coreapi.utils.packet.RatedPlayerInfo;
import org.bson.Document;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author NickM13
 * @since 4/27/2020
 */
public class Leaderboards {
    
    private final Map<String, LeaderboardCollection> LEADERBOARDS = new HashMap<>();
    
    private MongoCollection<Document> leaderboardCol;
    
    private BukkitTask decayTask;

    public Leaderboards() {
        init();
    }

    public void init() {
        leaderboardCol = Core.getInstance().getPluginDB().getCollection("Leaderboards");
        refresh();
    }

    public void refresh() {
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

    public void refresh(Set<UUID> players) {
        Map<UUID, CorePlayer> playerMap = new HashMap<>();
        for (UUID uuid : players) {
            playerMap.put(uuid, Core.getInstance().getPlayers().get(uuid));
        }
        for (LeaderboardCollection leaderboard : LEADERBOARDS.values()) {
            String name = leaderboard.getName();
            int season = leaderboard.getActive().getSeason();
            Set<RatedPlayerInfo> ratedPlayerInfos = new HashSet<>();
            for (CorePlayer cp : playerMap.values()) {
                PlayerRatings ratings = cp.getRatings();
                if (ratings.isRanked(name, season) && ratings.getGamesPlayed(name, season) > 0) {
                    ratedPlayerInfos.add(new RatedPlayerInfo(cp.getUniqueId(), ratings.getElo(name, season)));
                }
            }
            leaderboard.getActive().refreshPlayers(ratedPlayerInfos);
        }
    }

    public void close() {

    }
    
    public LeaderboardCollection get(String name) {
        if (!LEADERBOARDS.containsKey(name)) {
            LEADERBOARDS.put(name, new LeaderboardCollection(name));
        }
        return LEADERBOARDS.get(name);
    }
    
}
