/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.util.variable;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.io.converter.LocationConverter;
import com.spleefleague.core.menu.*;
import com.spleefleague.core.player.CorePlayer;
import com.spleefleague.core.player.rank.Rank;

import java.util.*;
import java.util.stream.Collectors;

import com.spleefleague.coreapi.database.annotation.DBField;
import com.spleefleague.coreapi.database.variable.DBEntity;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class Warp extends DBEntity {
    
    private static final SortedMap<String, Warp> warps = new TreeMap<>();
    private static MongoCollection<Document> warpCollection;
    private static final SortedMap<String, SortedSet<String>> folders = new TreeMap<>();
    private static final String DEFAULT_FOLDER = ".";
    
    /**
     * Loads all warps from the Warps collection
     */
    public static void init() {
        warpCollection = Core.getInstance().getPluginDB().getCollection("Warps");
        folders.put(DEFAULT_FOLDER, new TreeSet<>());
        for (Document doc : warpCollection.find()) {
            Warp warp = new Warp();
            warp.load(doc);
            warps.put(warp.getIdentifier(), warp);
        }
    }

    public static InventoryMenuContainerChest createMenuContainer(String folderName) {
        InventoryMenuContainerChest menuContainer = InventoryMenuAPI.createContainer()
                .setTitle(cp -> " Folder " + cp.getMenuTag("folderName", String.class))
                .setPageBoundaries(1, 3, 1, 7)
                .setOpenAction((container, cp) -> {
                    cp.setMenuTag("folderName", folderName == null ? DEFAULT_FOLDER : folderName);
                    cp.setMenuTag("warpPage", 0);
                })
                .setRefreshAction((container, cp) -> {
                    container.clearUnsorted();
                    int page = cp.getMenuTag("warpPage", Integer.class);
                    SortedSet<String> warpNames = folders.get(cp.getMenuTag("folderName", String.class));
                    for (String warpName : warpNames) {
                        Warp warp = warps.get(warpName);
                        container.addMenuItem(InventoryMenuAPI.createItem()
                                .setName(ChatColor.YELLOW + "" + ChatColor.BOLD + warp.getName())
                                .setDisplayItem(Material.SAND)
                                .setAction(cp2 -> cp2.warp(warp)));
                    }
                });

        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(ChatColor.RED + "" + ChatColor.BOLD + "Previous Page")
                        .setDescription("")
                        .setDisplayItem(InventoryMenuUtils.MenuIcon.PREVIOUS.getIconItem()),
                2, 4)
                .setVisibility(cp -> cp.getMenuTag("warpPage", Integer.class) > 0)
                .setAction(cp -> cp.setMenuTag("warpPage", cp.getMenuTag("warpPage", Integer.class) - 1))
                .setCloseOnAction(false);

        menuContainer.addStaticItem(InventoryMenuAPI.createItem()
                        .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Next Page")
                        .setDescription("")
                        .setDisplayItem(InventoryMenuUtils.MenuIcon.NEXT.getIconItem()),
                6, 4)
                .setVisibility(cp -> cp.getMenuTag("warpPage", Integer.class) < folders.get(cp.getMenuTag("folderName", String.class)).size() / menuContainer.getPageItemTotal())
                .setAction(cp -> cp.setMenuTag("warpPage", cp.getMenuTag("warpPage", Integer.class) + 1))
                .setCloseOnAction(false);

        return menuContainer;
    }

    public static void close() {

    }
    
    /**
     * Returns all warps available to a player, used for command tab completes
     *
     * @param cp Core Player
     * @return Warp Name Set
     */
    public static Set<String> getWarpNames(CorePlayer cp) {
        Set<String> warpNames = new HashSet<>();
        for (Warp warp : warps.values()) {
            if (warp.isAvailable(cp)) {
                warpNames.add(warp.getIdentifier());
            }
        }
        return warpNames;
    }
    
    /**
     * Return all warp names, used in command tab completes
     *
     * @return Warp Name Set
     */
    public static Set<Warp> getWarps(String folderName) {
        return new TreeSet<>(folders.get(folderName).stream().map(warps::get).collect(Collectors.toSet()));
    }
    
    /**
     * Returns all warps in TextComponent format to allow
     * clicking on links names to warp instead of having to type the warp name
     *
     * @return Text Component
     */
    public static TextComponent getWarpsFormatted() {
        TextComponent message = new TextComponent("");
        TextComponent warpstr;
        
        Iterator<HashMap.Entry<String, Warp>> wit = warps.entrySet().iterator();
        
        boolean first = true;
        while (wit.hasNext()) {
        Warp warp = wit.next().getValue();
            if (!first) {
                message.addExtra(new TextComponent(", "));
            } else first = false;
            warpstr = new TextComponent(warp.getIdentifier());
            warpstr.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to warp to '" + warpstr.getText() + "'").create()));
            warpstr.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + warp.getIdentifier()));
            warpstr.setColor(Chat.getColor("DEFAULT").asBungee());
            message.addExtra(warpstr);
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
        return warps.get(name);
    }
    
    /**
     * Creates a warp by a name at a location
     *
     * @param identifier Warp Name
     * @param loc Location
     */
    public static int setWarp(String identifier, Location loc) {
        if (identifier.contains(":")) {
            String[] args = identifier.split(":", 2);
            if (args[0].isEmpty() || args[1].isEmpty()) {
                return 1;
            }
        }
        if (warps.containsKey(identifier)) {
            delWarp(identifier);
            return 2;
        }
        Warp warp = new Warp(identifier, loc);
        warps.put(warp.getIdentifier(), warp);
        warp.save(warpCollection);
        return 0;
    }
    
    /**
     * Deletes a warp by name
     *
     * @param identifier Warp Name
     * @return Success
     */
    public static boolean delWarp(String identifier) {
        if (warps.containsKey(identifier)) {
            Warp warp = warps.remove(identifier);
            warp.unsave(warpCollection);
            folders.get(warp.getFolder()).remove(identifier);
            if (folders.get(warp.getFolder()).isEmpty() && !warp.getFolder().equals(DEFAULT_FOLDER)) {
                folders.remove(warp.getFolder());
            }
            return true;
        }
        return false;
    }

    @DBField(serializer=LocationConverter.class)
    public Location location;
    protected String warpName;
    protected String folderName = null;
    
    public Warp() {

    }

    public Warp(String identifier, Location location) {
        this.identifier = identifier;
        this.location = location;
        initFolder();
    }
    
    @Override
    public void afterLoad() {
        initFolder();
    }

    private void initFolder() {
        if (identifier.contains(":")) {
            String[] args = identifier.split(":", 2);
            if (!args[0].isEmpty() && !args[1].isEmpty()) {
                folderName = args[0];
                warpName = args[1];
                if (!folders.containsKey(folderName)) {
                    folders.put(folderName, new TreeSet<>());
                }
                folders.get(folderName).add(identifier);
                return;
            }
        }
        warpName = identifier;
        folderName = DEFAULT_FOLDER;
        folders.get(folderName).add(identifier);
    }

    public String getName() {
        return warpName;
    }

    public String getFolder() {
        return folderName;
    }

    public Location getLocation() {
        return location;
    }
    public boolean isAvailable(CorePlayer cp) {
        Rank rank = Rank.getRank(folderName);
        if (rank == null) {
            return cp.getRank().hasPermission(Rank.MODERATOR, Lists.newArrayList(Rank.BUILDER));
        } else {
            return cp.getRank().hasPermission(rank);
        }
    }

}