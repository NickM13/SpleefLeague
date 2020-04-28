/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.database.annotation.DBField;
import com.spleefleague.core.database.variable.DBEntity;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainer;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

import java.util.*;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * @author NickM13
 */
public class Warp extends DBEntity {
    
    private static final Map<String, Warp> warps = new TreeMap<>();
    private static final Map<String, Set<String>> folders = new TreeMap<>();
    private static MongoCollection<Document> warpCollection;
    
    private static final String DEFAULT_FOLDER = "..";
    
    /**
     * Loads all warps from the Warps collection
     */
    public static void init() {
        warpCollection = Core.getInstance().getPluginDB().getCollection("Warps");
        MongoCursor<Document> it = warpCollection.find().iterator();
        createFolder(DEFAULT_FOLDER);
        while (it.hasNext()) {
            Document doc = it.next();
            Warp warp = new Warp();
            warp.load(doc);
            createFolder(warp.getFolder());
            folders.get(warp.getFolder()).add(warp.getName().toLowerCase());
            warps.put(warp.getName().toLowerCase(), warp);
        }
    }
    
    /**
     * Creates a warp menu of warps available to a player
     *
     * @return Menu Container
     */
    public static InventoryMenuContainer createAvailableWarpMenu() {
        return InventoryMenuAPI.createContainer()
                .setTitle("Warp Menu")
                .setOpenAction((container, cp) -> {
                    container.clearUnsorted();
                    for (Map.Entry<String, Set<String>> folder1 : folders.entrySet()) {
                        InventoryMenuItem folderMenu = InventoryMenuAPI.createItem()
                                .setName(folder1.getKey())
                                .setDisplayItem(Material.CHEST_MINECART)
                                .setDescription("Open Warp Folder")
                                .createLinkedContainer(folder1.getKey());
                        for (String warpName : folder1.getValue()) {
                            folderMenu.getLinkedContainer()
                                    .addMenuItem(InventoryMenuAPI.createItem()
                                    .setDisplayItem(warps.get(warpName).getDisplayItem())
                                    .setName(warps.get(warpName).getName())
                                    .setAction(cp2 -> cp2.warp(warps.get(warpName))));
                        }
                        container.addMenuItem(folderMenu);
                    }
                });
    }
    
    /**
     * Saves all warps that were changed
     */
    public static void close() {
        if (warpCollection == null) return;
        
        for (HashMap.Entry<String, Warp> w : warps.entrySet()) {
            if (w.getValue().hasChanged) {
                List<Object> loc = LocationConverter.save(w.getValue().location);
                if (loc != null) {
                    if (warpCollection.find(new Document("name", w.getValue().name)).first() != null) {
                        warpCollection.deleteMany(new Document("name", w.getValue().name));
                    }
                    warpCollection.insertOne(w.getValue().save());
                }
            }
        }
    }
    
    /**
     * Creates a new folder if it doesn't already exist to keep warps in
     *
     * @param folder Folder
     */
    public static void createFolder(String folder) {
        if (folders.containsKey(folder.toLowerCase())) return;
        folders.put(folder, new TreeSet<>());
    }
    
    /**
     * Deletes a folder and moves all contained warps to DEFAULT_FOLDER
     *
     * @param folder Folder
     */
    public static boolean deleteFolder(String folder) {
        if (folder.equals(DEFAULT_FOLDER)) {
            return false;
        }
        Set<String> warpNames = new HashSet<>(folders.get(folder.toLowerCase()));
        for (String warpName : warpNames) {
            moveWarp(warpName, DEFAULT_FOLDER);
        }
        folders.remove(folder.toLowerCase());
        return true;
    }
    
    /**
     * Moves a warp to a folder
     *
     * @param warpName Warp Name
     * @param folder Folder
     */
    public static void moveWarp(String warpName, String folder) {
        folder = folder.toLowerCase();
        if (!folders.containsKey(folder)) folders.put(folder, new HashSet<>());
        Warp warp = getWarp(warpName);
        folders.get(warp.getFolder()).remove(warpName);
        folders.get(folder).add(warpName);
        warp.setFolder(folder);
    }
    
    /**
     * Returns all warps available to a player, used for command tab completes
     *
     * @param cp Core Player
     * @return Warp Name Set
     */
    public static Set<String> getWarpNames(CorePlayer cp) {
        Set<String> warpFiled = new HashSet<>();
        for (Warp w : warps.values()) {
            if (w.isAvailable(cp)) {
                if (w.getFolder().equals(DEFAULT_FOLDER)) {
                    warpFiled.add(w.getName());
                } else {
                    warpFiled.add(w.getFolder() + ":" + w.getName());
                }
            }
        }
        return warpFiled;
    }
    
    /**
     * Returns all folder names, used in command tab completes
     *
     * @return Folder Name Set
     */
    public static Set<String> getWarpFolders() {
        return folders.keySet();
    }
    
    /**
     * Return all warp names, used in command tab completes
     *
     * @return Warp Name Set
     */
    public static Set<Warp> getWarps() {
        return Sets.newHashSet(warps.values());
    }
    
    /**
     * Returns all warps in a folder in TextComponent format to allow
     * clicking on links names to warp instead of having to type the warp name
     *
     * @param folder Folder
     * @return Text Component
     */
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
    
    /**
     * Gets a warp by name
     *
     * @param name Warp Name
     * @return Warp
     */
    public static Warp getWarp(String name) {
        String[] a = name.split(":", 2);
        return warps.get(a[a.length-1].toLowerCase());
    }
    
    /**
     * Get all warps in a folder
     *
     * @param folderName Folder Name
     * @return Warp Set
     */
    public static Set<Warp> getWarps(String folderName) {
        Set<Warp> folderedWarps = new HashSet<>();
        Set<String> folder = folders.get(folderName);
        if (folder != null) {
            for (String name : folder) {
                folderedWarps.add(warps.get(name));
            }
        }
        return folderedWarps;
    }
    
    /**
     * Creates a warp by a name at a location, folder can be
     * set using a : to split, e.g /setwarp fold:newwarp
     *
     * @param name Warp Name
     * @param loc Location
     */
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
        Warp warp = new Warp(warpname, loc, warpfolder);
        warp.hasChanged = true;
        warps.put(warpname.toLowerCase(), warp);
        if (!warpfolder.equals(DEFAULT_FOLDER)) {
            folders.get(warpfolder).add(warp.getName());
        }
    }
    
    /**
     * Deletes a warp by name
     *
     * @param name Warp Name
     * @return Success
     */
    public static boolean delWarp(String name) {
        if (warps.containsKey(name.toLowerCase())) {
            warpCollection.deleteOne(new Document("name", warps.get(name.toLowerCase()).name));
            warps.remove(name.toLowerCase());
            return true;
        }
        return false;
    }
    
    @DBField
    public String name;
    @DBField(serializer=LocationConverter.class)
    public Location location;
    @DBField
    public String folder;
    public boolean hasChanged;
    public ItemStack displayItem;
    @DBField
    public UUID uuid;
    
    private Warp() {
    
    }
    private Warp(String name, Location location, String folder) {
        this.name = name;
        this.location = location;
        this.hasChanged = false;
        if (folder == null)     this.folder = DEFAULT_FOLDER;
        else                    this.folder = folder;
    }
    
    @Override
    public void afterLoad() {
        if (uuid != null) {
            this.displayItem = InventoryMenuUtils.createCustomSkull(uuid);
        } else {
            displayItem = new ItemStack(Material.PLAYER_HEAD);
        }
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
    public boolean isAvailable(CorePlayer cp) {
        Rank rank = Rank.getRank(folder);
        if (rank == null) {
            return cp.getRank().hasPermission(Rank.MODERATOR, Lists.newArrayList(Rank.BUILDER));
        } else {
            return cp.getRank().hasPermission(rank);
        }
    }
    public ItemStack getDisplayItem() {
        if (displayItem == null) return new ItemStack(Material.PLAYER_HEAD);
        return displayItem;
    }
    public void setDisplayItem(String username) {
        hasChanged = true;
        this.uuid = Bukkit.getOfflinePlayer(username).getUniqueId();
        this.displayItem = InventoryMenuUtils.createCustomSkull(this.uuid);
    }
    
}
