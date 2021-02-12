package com.spleefleague.core.game.history;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

import java.util.*;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class GameHistory extends DBEntity {

    public static class Player extends DBVariable<Document> {

        UUID uuid;
        int place = -1;
        int score = -1;
        Document additional = new Document();

        public Player(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void load(Document document) {

        }

        @Override
        public Document save() {
            Document doc = new Document("uuid", uuid.toString()).append("place", place).append("score", score);
            additional.forEach(doc::append);
            return doc;
        }

    }

    public enum EndReason {
        NONE,
        CANCEL,
        FORFEIT,
        ENDGAME,
        NORMAL
    }

    @DBField
    private final String mode;
    @DBField
    private final String arena;
    @DBField
    private final Integer avgRating;
    private final Map<UUID, Integer> playerMap = new HashMap<>();
    @DBField
    private final List<Player> players = new ArrayList<>();
    @DBField
    private EndReason endReason = EndReason.NONE;
    @DBField
    private final Long startTime;
    @DBField
    private Long endTime;

    public GameHistory(UUID battleId, List<UUID> players, String mode, String arena, int avgRating) {
        this.identifier = battleId.toString();
        for (UUID uuid : players) {
            this.playerMap.put(uuid, this.players.size());
            this.players.add(new Player(uuid));
        }
        this.mode = mode;
        this.arena = arena;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0L;
        this.avgRating = avgRating;
    }

    public boolean isValid() {
        return endReason != EndReason.NONE;
    }

    public void setEndReason(EndReason endReason) {
        this.endReason = endReason;
        this.endTime = System.currentTimeMillis();
    }

    public void setPlayerStats(UUID uuid, int place, int score) {
        players.get(playerMap.get(uuid)).place = place;
        players.get(playerMap.get(uuid)).score = score;
    }

    public void addPlayerAdditional(UUID uuid, String fieldName, Object obj) {
        players.get(playerMap.get(uuid)).additional.append(fieldName, obj);
    }

}