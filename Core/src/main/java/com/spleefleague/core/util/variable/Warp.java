/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.Rank;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Location;

/**
 * @author NickM13
 */
public class Warp {
    
    private static final Map<String, Warp> warps = new TreeMap<>();
    private static final Map<String, Set<Warp>> folders = new TreeMap<>();
    private static MongoCollection<Document> warpCollection;
    
    private static final String DEFAULT_FOLDER = "..";
    
    public static void init() {
        warpCollection = Core.getInstance().getPluginDB().getCollection("Warps");
        MongoCursor<Document> it = warpCollection.find().iterator();
        createFolder(DEFAULT_FOLDER);
        while (it.hasNext()) {
            Document doc = it.next();
            Warp warp = new Warp(doc);
            createFolder(warp.getFolder());
            folders.get(warp.getFolder()).add(warp);
            warps.put(warp.getName().toLowerCase(), warp);
        }
    }
    public static void close() {
        if (warpCollection == null) return;
        
        for (HashMap.Entry<String, Warp> w : warps.entrySet()) {
            if (w.getValue().hasChanged) {
                List<Object> loc = LocationConverter.save(w.getValue().location);
                if (loc != null) {
                    if (warpCollection.find(new Document("name", w.getValue().name)).first() != null) {
                        warpCollection.deleteMany(new Document("name", w.getValue().name));
                    }
                    warpCollection.insertOne(new Document("name", w.getValue().name).append("location", loc).append("folder", w.getValue().getFolder()));
                }
            }
        }
    }
    
    public static void createFolder(String folder) {
        if (folders.containsKey(folder.toLowerCase())) return;
        folders.put(folder, new HashSet<>());
    }
    public static void deleteFolder(String folder) {
        if (folder.equals(DEFAULT_FOLDER)) {
            return;
        }
        for (Warp w : folders.get(folder.toLowerCase())) {
            w.setFolder(DEFAULT_FOLDER);
        }
    }
    public static void moveWarp(String warpName, String folder) {
        folder = folder.toLowerCase();
        if (!folders.containsKey(folder)) folders.put(folder, new HashSet<>());
        Warp warp = getWarp(warpName);
        folders.get(warp.getFolder()).remove(warp);
        folders.get(folder).add(warp);
        warp.setFolder(folder);
    }
    
    public static Set<String> getWarpNames(CorePlayer cp) {
        Set<String> warpFiled = new HashSet<>();
        for (Warp w : warps.values()) {
            if (cp.getRank().hasPermission(w.getMinRank())) {
                if (w.getFolder().equals(DEFAULT_FOLDER)) {
                    warpFiled.add(w.getName());
                } else {
                    warpFiled.add(w.getFolder() + ":" + w.getName());
                }
            }
        }
        return warpFiled;
    }
    public static Set<String> getWarpFolders() {
        return folders.keySet();
    }
    public static Set<Warp> getWarps() {
        return Sets.newHashSet(warps.values());
    }
    
    public static TextComponent getWarpsFormatted(Rank rank) {
        TextComponent message = new TextComponent("");
        TextComponent warpstr;
        
        Iterator<HashMap.Entry<String, Warp>> wit = warps.entrySet().iterator();
        
        while (wit.hasNext()) {
            Warp warp = wit.next().getValue();
            if (!rank.hasPermission(warp.getMinRank()))
                continue;
            warpstr = new TextComponent(warp.name);
            warpstr.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to warp to '" + warpstr.getText() + "'").create()));
            warpstr.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.name));
            warpstr.setColor(Chat.getColor("DEFAULT").asBungee());
            message.addExtra(warpstr);
            if (wit.hasNext()) {
                message.addExtra(new TextComponent(", "));
            }
        }
        
        return message;
    }
    public static TextComponent getWarpsFormatted(String folder) {
        TextComponent message = new TextComponent("");
        TextComponent warpstr;
        
        Iterator<HashMap.Entry<String, Warp>> wit = warps.entrySet().iterator();
        
        boolean first = true;
        while (wit.hasNext()) {
            Warp warp = wit.next().getValue();
            if (warp.getFolder().equalsIgnoreCase(folder)) {
                if (!first) {
                    message.addExtra(new TextComponent(", "));
                } else first = false;
                warpstr = new TextComponent(warp.name);
                warpstr.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to warp to '" + warpstr.getText() + "'").create()));
                warpstr.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.name));
                warpstr.setColor(Chat.getColor("DEFAULT").asBungee());
                message.addExtra(warpstr);
            }
        }
        
        return message;
    }
    
    public static Warp getWarp(String name) {
        String a[] = name.split(":", 2);
        return warps.get(a[a.length-1].toLowerCase());
    }
    public static Set<Warp> getWarps(String folder) {
        return folders.get(folder);
    }
    public static void setWarp(String name, Location loc) {
        String warpfolder = DEFAULT_FOLDER;
        String warpname = name;
        if (name.contains(":")) {
            warpfolder = name.split(":")[0];
            warpname = name.split(":")[1];
            if (warpfolder.isEmpty() || warpname.isEmpty()) {
                return;
            }
        }
        Warp warp = new Warp(warpname, loc, warpfolder, Rank.MODERATOR);
        warp.hasChanged = true;
        warps.put(warpname.toLowerCase(), warp);
        if (!warpfolder.equals(DEFAULT_FOLDER)) {
            folders.get(warpfolder).add(warp);
        }
    }
    public static boolean delWarp(String name) {
        if (warps.containsKey(name.toLowerCase())) {
            warpCollection.deleteOne(new Document("name", warps.get(name.toLowerCase()).name));
            warps.remove(name.toLowerCase());
            return true;
        }
        return false;
    }
    
    public String name;
    public Location location;
    public String folder;
    public boolean hasChanged;
    public Rank minRank;
    
    private Warp(Document doc) {
        this(doc.get("name", String.class),
                LocationConverter.load(doc.get("location", List.class)),
                doc.get("folder", String.class),
                Rank.getRank(doc.get("minRank", String.class)));
    }
    private Warp(String name, Location location, String folder, Rank minRank) {
        this.name = name;
        this.location = location;
        this.hasChanged = false;
        if (folder == null)     this.folder = DEFAULT_FOLDER;
        else                    this.folder = folder;
        if (minRank == null)    this.minRank = Rank.MODERATOR;
        else                    this.minRank = minRank;
    }
    
    public String getName() {
        return name;
    }
    public Location getLocation() {
        return location;
    }
    public void setFolder(String folder) {
        this.folder = folder;
        hasChanged = true;
    }
    public String getFolder() {
        return folder;
    }
    public void setMinRank(Rank minRank) {
        this.minRank = minRank;
    }
    public Rank getMinRank() {
        return minRank;
    }
    
}
