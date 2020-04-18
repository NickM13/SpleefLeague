/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.player.infraction;

import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bson.Document;
import org.bukkit.ChatColor;

/**
 * @author NickM13
 */
public class Infraction {
    
    private static MongoCollection<Document> collection;
    private static HashSet<Infraction> infractions = new HashSet<>();
    //private static HashSet<Infraction> infractionsNew = new HashSet<>();
    private static HashMap<UUID, HashMap<Type, Infraction>> infractionsActive = new HashMap<>();
    
    public static void init() {
        collection = Core.getInstance().getPluginDB().getCollection("Infractions");
        collection.find().iterator().forEachRemaining(doc -> {
            Infraction infraction = new Infraction(doc);
            Type type = infraction.getType();
            UUID uuid = UUID.fromString(doc.get("uuid", String.class));
            infractions.add(infraction);
            if (!infractionsActive.containsKey(uuid)) {
                infractionsActive.put(uuid, new HashMap<>());
            }
            if (!infractionsActive.get(uuid).containsKey(type) ||
                    infractionsActive.get(uuid).get(type).getTime() < infraction.getTime()) {
                infractionsActive.get(uuid).put(type, infraction);
            }
        });
    }
    public static void close() {
        
    }
    
    public enum Type {
        WARNING("Warning", ChatColor.YELLOW),
        KICK("Kick", ChatColor.GOLD),
        TEMPBAN("Tempban", ChatColor.RED),
        BAN("Ban", ChatColor.DARK_RED),
        UNBAN("Unban", ChatColor.GREEN),
        MUTE_PUBLIC("PubMute", ChatColor.GRAY),
        MUTE_SECRET("SecMute", ChatColor.BLACK);

        private final String name;
        private final ChatColor color;

        private Type(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public ChatColor getColor() {
            return color;
        }
    }
    
    private UUID uuid;
    private String punisher;
    private Type type;
    private long time, duration;
    private String reason;
    
    public Infraction() {
        time = System.currentTimeMillis();
    }
    public Infraction(UUID uuid, String punisher, Type type, long time, long duration, String message) {
        this.uuid = uuid;
        this.punisher = punisher;
        this.type = type;
        this.time = time;
        this.duration = duration;
        this.reason = message;
    }
    public Infraction(Document doc) {
        this.uuid = UUID.fromString(doc.get("uuid", String.class));
        this.punisher = doc.get("punisher", String.class);
        this.type = Type.valueOf(doc.get("type", String.class));
        this.time = doc.get("time", Long.class);
        this.duration = doc.get("duration", Long.class);
        this.reason = doc.get("message", String.class);
    }
    
    public static void create(Infraction i) {
        if (!infractionsActive.containsKey(i.uuid)) {
            infractionsActive.put(i.uuid, new HashMap<>());
        }
        infractionsActive.get(i.uuid).put(i.type, i);
        infractions.add(i);
        collection.insertOne(i.save());
    }
    public static Infraction getActive(UUID uniqueId, Type type) {
        if (!infractionsActive.containsKey(uniqueId)) return null;
        return infractionsActive.get(uniqueId).get(type);
    }
    public static Infraction getMostRecent(UUID uniqueId, List<Type> types) {
        Infraction recent = null, infraction;
        for (Type type : types) {
            infraction = getActive(uniqueId, type);
            if (recent == null
                    || infraction.time > recent.time) {
                recent = infraction;
            }
        }
        return recent;
    }
    public static List<Infraction> getAll(UUID uniqueId) {
        List<Infraction> list = new ArrayList<>();
        
        infractions.forEach(i -> {
            if (i.uuid.equals(uniqueId))
                list.add(i);
        });
        
        return list;
    }
    public Infraction setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }
    public UUID getUuid() {
        return uuid;
    }
    public Infraction setPunisher(String punisher) {
        this.punisher = punisher;
        return this;
    }
    public String getPunisher() {
        return punisher;
    }
    public Infraction setType(Type type) {
        this.type = type;
        return this;
    }
    public Type getType() {
        return type;
    }
    public Infraction setTime(long time) {
        this.time = time;
        return this;
    }
    public Infraction setDuration(long duration) {
        this.duration = duration;
        return this;
    }
    public long getTime() {
        return time;
    }
    public long getExpireTime() {
        return time + duration;
    }
    public boolean isExpired() {
        return getRemainingTime() < 0;
    }
    public long getRemainingTime() {
        return (getExpireTime() - System.currentTimeMillis() + 500) / 1000;
    }
    public String getRemainingTimeString() {
        long sec = getRemainingTime();
        String str = "";
        
        // Days
        str += String.format("%02d", sec / 3600 / 24) + ":";
        // Hours
        str += String.format("%02d", sec / 3600 % 24) + ":";
        // Minutes
        str += String.format("%02d", sec / 60 % 60) + ":";
        // Seconds
        str += String.format("%02d", sec % 60);
        
        return str;
    }
    public Infraction setReason(String reason) {
        this.reason = reason;
        return this;
    }
    public String getReason() {
        return reason;
    }
    
    private Document save() {
        Document doc = new Document("uuid", uuid.toString());
        doc.append("punisher", punisher != null ? punisher.toString() : "");
        doc.append("type", type.toString());
        doc.append("time", time);
        doc.append("duration", duration);
        doc.append("message", reason);
        return doc;
    }
    
}
