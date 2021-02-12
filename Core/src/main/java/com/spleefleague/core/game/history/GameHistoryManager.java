package com.spleefleague.core.game.history;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class GameHistoryManager {

    private static MongoCollection<Document> gameHistoryCol;

    public static void init() {
        gameHistoryCol = Core.getInstance().getPluginDB().getCollection("GameHistory");
    }

    public static void close() {

    }

    public static void addHistory(GameHistory gameHistory) {
        if (gameHistory.isValid()) {
            gameHistoryCol.insertOne(gameHistory.toDocument());
        }
    }

}
