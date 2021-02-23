package com.spleefleague.core.settings;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import org.bson.Document;

import java.util.Objects;

/**
 * @author NickM13
 */
public class Settings {

    private static MongoCollection<Document> settingsCollection;

    private static Discord discord;

    private static String season;

    public static void init() {
        settingsCollection = Core.getInstance().getPluginDB().getCollection("Settings");
        discord = new Discord();
        Document doc = settingsCollection.find(new Document("identifier", discord.getIdentifier())).first();
        if (doc != null) discord.load(doc);
        doc = settingsCollection.find(new Document("identifier", "season")).first();
        if (doc != null) season = doc.getString("current");
        else season = "preseason1";
    }

    public static void setDiscord(String url) {
        discord.setUrl(url);
        discord.save(settingsCollection);
    }

    public static Discord getDiscord() {
        return discord;
    }

    public static String getCurrentSeason() {
        return season;
    }

}
