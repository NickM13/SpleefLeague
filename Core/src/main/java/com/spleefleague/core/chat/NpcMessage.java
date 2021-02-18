package com.spleefleague.core.chat;

import com.google.common.collect.Lists;
import com.spleefleague.coreapi.database.variable.DBVariable;
import org.bson.Document;

import java.util.List;

/**
 * @author NickM13
 * @since 2/16/2021
 */
public class NpcMessage extends DBVariable<Document> {

    private String profile;
    private String name;
    private List<String> messages;

    public static NpcMessage fromCommand(String profile, String name, String message) {
        return new NpcMessage(profile, name.replaceAll("_", " "), Lists.newArrayList(message.split("\\\\n")));
    }

    public NpcMessage() {

    }

    protected NpcMessage(String profile, String name, List<String> messages) {
        this.profile = profile;
        this.name = name;
        this.messages = messages;
    }

    public String getProfile() {
        return profile;
    }

    public String getName() {
        return name;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void load(Document document) {
        this.profile = document.get("profile", String.class);
        this.name = document.get("name", String.class);
        this.messages = document.getList("messages", String.class);
    }

    @Override
    public Document save() {
        return new Document("profile", profile).append("name", name).append("messages", messages);
    }
}
