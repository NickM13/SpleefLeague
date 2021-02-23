package com.spleefleague.coreapi.game.leaderboard;

import com.spleefleague.coreapi.chat.Chat;
import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.player.statistics.Rating;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public class Leaderboard extends DBEntity {

    public static class LeaderboardEntry extends DBEntity {

        private final UUID uuid;
        @DBField private String username;
        @DBField private Integer elo;
        @DBField private Rating.Division division;
        @DBField private Integer wins;
        @DBField private Integer losses;
        @DBField private Long lastPlayed;
        @DBField private Long lastDecay;

        public LeaderboardEntry(UUID uuid) {
            this.uuid = uuid;
        }

        public LeaderboardEntry(UUID uuid, String username, Rating rating) {
            this.uuid = uuid;
            this.username = username;
            update(rating);
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public String getUsername() {
            return username;
        }

        public String getDisplayElo() {
            return Chat.BRACKET + "(" + division.getScorePrefix() + elo + Chat.BRACKET + ")";
        }

        public int getElo() {
            return elo;
        }

        public Rating.Division getDivision() {
            return division;
        }

        public int getWins() {
            return wins;
        }

        public int getLosses() {
            return losses;
        }

        public long getLastPlayed() {
            return lastPlayed;
        }

        public long getLastDecay() {
            return lastDecay;
        }

        public void update(Rating rating) {
            this.elo = rating.getElo();
            this.division = rating.getDivision();
            this.wins = rating.getWins();
            this.losses = rating.getLosses();
            this.lastPlayed = System.currentTimeMillis();
            this.lastDecay = 0L;
        }

    }

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd MMMMMMMMM, yyyy");

    @DBField protected String name;
    @DBField protected String season;
    @DBField protected Long createTime;

    protected final Map<UUID, Integer> playerScoreMap;
    protected final SortedMap<Integer, LinkedHashMap<UUID, LeaderboardEntry>> scorePlayersMap;

    public Leaderboard() {
        playerScoreMap = new HashMap<>();
        scorePlayersMap = new TreeMap<>(Collections.reverseOrder());
    }

    public Leaderboard(String name, String season) {
        this.name = name;
        this.season = season;
        playerScoreMap = new HashMap<>();
        scorePlayersMap = new TreeMap<>(Collections.reverseOrder());
    }

    public String getName() {
        return name;
    }

    /**
     * Time in millis when this leaderboard was created
     *
     * @return Millis Created Time
     */
    public long getCreateTime() {
        return createTime;
    }

    public String getSeason() {
        return season;
    }

    /**
     * Retrieve a section of players by their ranks
     *
     * @param startPlace Starting Rank #
     * @param count Max Number of Players
     * @return Player UUIDs
     */
    public List<LeaderboardEntry> getPlayers(int startPlace, int count) {
        List<LeaderboardEntry> playerSection = new ArrayList<>();

        int lastSectPlace = 0;
        int currPlace = 0;
        Iterator<Map.Entry<Integer, LinkedHashMap<UUID, LeaderboardEntry>>> spit = scorePlayersMap.entrySet().iterator();
        while (spit.hasNext() && currPlace < startPlace + count) {
            Map.Entry<Integer, LinkedHashMap<UUID, LeaderboardEntry>> entry = spit.next();
            lastSectPlace += entry.getValue().size();
            if (lastSectPlace > startPlace) {
                for (LeaderboardEntry leaderboardEntry : entry.getValue().values()) {
                    if (currPlace >= startPlace) {
                        playerSection.add(leaderboardEntry);
                        if (playerSection.size() >= count) {
                            return playerSection;
                        }
                    }
                    currPlace++;
                }
            } else {
                currPlace += entry.getValue().size();
            }
        }

        return playerSection;
    }

    public boolean containsPlayer(UUID player) {
        return playerScoreMap.containsKey(player);
    }

    /**
     * Returns total number of players in this leaderboard
     *
     * @return Number of Players
     */
    public int getPlayerCount() {
        return playerScoreMap.size();
    }

    /**
     * Calculates the player's ranked ladder position by accumulating the sizes of all previous leaderboard score sets
     *
     * @param player Player UUID
     * @return Ladder Rank
     */
    public int getPlayerRanking(UUID player) {
        if (!playerScoreMap.containsKey(player)) {
            return -1;
        }
        int placement = 0;
        for (Map.Entry<Integer, LinkedHashMap<UUID, LeaderboardEntry>> entry : scorePlayersMap.entrySet()) {
            if (!entry.getKey().equals(playerScoreMap.get(player))) {
                placement += entry.getValue().size();
            } else {
                // TODO: Do we need this specific of ranks?
                break;
            }
        }
        return placement;
    }

    public int getPlayerScore(UUID player) {
        return playerScoreMap.getOrDefault(player, 0);
    }

    /**
     * Inserts a player's score into the leaderboard, removing them first if they already existed
     *
     * @param player Player UUID
     * @param rating Score
     */
    public void setPlayerScore(UUID player, String username, Rating rating) {
        Integer prevScore = playerScoreMap.get(player);
        LeaderboardEntry entry;
        if (prevScore != null) {
            if (prevScore == rating.getElo()) return;
            entry = scorePlayersMap.get(prevScore).remove(player);
            entry.update(rating);
            if (scorePlayersMap.get(prevScore).isEmpty()) {
                scorePlayersMap.remove(prevScore);
            }
        } else {
            entry = new LeaderboardEntry(player, username, rating);
        }
        playerScoreMap.put(player, rating.getElo());
        if (!scorePlayersMap.containsKey(rating.getElo())) {
            scorePlayersMap.put(rating.getElo(), new LinkedHashMap<>());
        }
        scorePlayersMap.get(rating.getElo()).put(player, entry);
    }

    protected void setPlayerScore(UUID player, LeaderboardEntry entry) {
        Integer prevScore = playerScoreMap.get(player);
        if (prevScore != null) {
            if (entry.getElo() == prevScore) return;
            scorePlayersMap.get(prevScore).remove(player);
            if (scorePlayersMap.get(prevScore).isEmpty()) {
                scorePlayersMap.remove(prevScore);
            }
        }
        playerScoreMap.put(player, entry.getElo());
        if (!scorePlayersMap.containsKey(entry.getElo())) {
            scorePlayersMap.put(entry.getElo(), new LinkedHashMap<>());
        }
        scorePlayersMap.get(entry.getElo()).put(player, entry);
    }

}
