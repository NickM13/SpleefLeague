package com.spleefleague.proxycore.game.leaderboard;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.annotation.DBLoad;
import com.spleefleague.coreapi.database.annotation.DBSave;
import com.spleefleague.coreapi.database.variable.DBEntity;
import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author NickM13
 * @since 4/28/2020
 */
public abstract class Leaderboard extends DBEntity {

    protected static final DateFormat dateFormat = new SimpleDateFormat("dd MMMMMMMMM, yyyy");

    @DBField
    protected Boolean active;
    @DBField protected String name;
    @DBField protected Integer season;
    @DBField protected Long createTime;

    protected final HashMap<UUID, Integer> playerScoreMap;
    protected final TreeMap<Integer, LinkedHashSet<UUID>> scorePlayersMap;

    public Leaderboard() {
        playerScoreMap = new HashMap<>();
        scorePlayersMap = new TreeMap<>(Collections.reverseOrder());
    }

    public Leaderboard(String name, int season) {
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

    public boolean isActive() {
        return active;
    }

    public int getSeason() {
        return season;
    }

    /**
     * Retrieve a section of players by their ranks
     *
     * @param startPlace Starting Rank #
     * @param count Max Number of Players
     * @return Player UUIDs
     */
    public List<UUID> getPlayers(int startPlace, int count) {
        List<UUID> playerSection = new ArrayList<>();

        int lastSectPlace = 0;
        int currPlace = 0;
        Iterator<Map.Entry<Integer, LinkedHashSet<UUID>>> spit = scorePlayersMap.entrySet().iterator();
        while (spit.hasNext() && currPlace < startPlace + count) {
            Map.Entry<Integer, LinkedHashSet<UUID>> entry = spit.next();
            lastSectPlace += entry.getValue().size();
            if (lastSectPlace > startPlace) {
                for (UUID uuid : entry.getValue()) {
                    if (currPlace >= startPlace) {
                        playerSection.add(uuid);
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
    public int getPlayerRank(UUID player) {
        if (!playerScoreMap.containsKey(player)) {
            return -1;
        }
        int placement = 0;
        for (Map.Entry<Integer, LinkedHashSet<UUID>> entry : scorePlayersMap.entrySet()) {
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
     * @param score Score
     */
    public void setPlayerScore(UUID player, int score) {
        Integer prevScore = playerScoreMap.get(player);
        if (prevScore != null) {
            scorePlayersMap.get(prevScore).remove(player);
            if (scorePlayersMap.get(prevScore).isEmpty()) {
                scorePlayersMap.remove(prevScore);
            }
        }
        playerScoreMap.put(player, score);
        if (!scorePlayersMap.containsKey(score)) {
            scorePlayersMap.put(score, new LinkedHashSet<>());
        }
        scorePlayersMap.get(score).add(player);
    }

    /**
     * Sorts a map of players with new scores into the leaderboard
     *
     * @param oldScores Score Map of Score, Player Set
     * @param newScores Score Map of Score, Player Set
     */
    public void sortMany(Map<Integer, Set<UUID>> oldScores, Map<Integer, Set<UUID>> newScores) {
        for (Map.Entry<Integer, Set<UUID>> entry : oldScores.entrySet()) {
            if (scorePlayersMap.containsKey(entry.getKey())) {
                if (scorePlayersMap.get(entry.getKey()).size() <= entry.getValue().size()) {
                    scorePlayersMap.remove(entry.getKey());
                } else {
                    for (UUID uuid : entry.getValue()) {
                        scorePlayersMap.get(entry.getKey()).remove(uuid);
                    }
                }
            }
        }
        for (Map.Entry<Integer, Set<UUID>> entry : newScores.entrySet()) {
            if (!scorePlayersMap.containsKey(entry.getKey())) {
                scorePlayersMap.put(entry.getKey(), new LinkedHashSet<>());
            }
            for (UUID uuid : entry.getValue()) {
                scorePlayersMap.get(entry.getKey()).add(uuid);
                playerScoreMap.put(uuid, entry.getKey());
            }
        }
    }

    @DBSave(fieldName = "players")
    protected Document savePlayers() {
        Document doc = new Document();
        for (Map.Entry<UUID, Integer> entry : playerScoreMap.entrySet()) {
            doc.append(entry.getKey().toString(), entry.getValue());
        }
        return doc;
    }

    @DBLoad(fieldName = "players")
    protected void loadPlayers(Document doc) {
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            setPlayerScore(UUID.fromString(entry.getKey()), (Integer) entry.getValue());
        }
    }

    public abstract String getDescription();

}
